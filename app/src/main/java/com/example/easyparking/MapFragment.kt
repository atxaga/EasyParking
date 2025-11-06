package com.example.easyparking

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import java.io.BufferedReader
import java.io.InputStreamReader

class MapFragment : Fragment() {

    private lateinit var map: MapView
    private var selectedPolygon: Polygon? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        map = view.findViewById(R.id.mapView)

        Configuration.getInstance().userAgentValue = requireContext().packageName
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val donostia = GeoPoint(43.321, -1.985)
        map.controller.setZoom(16.0)
        map.controller.setCenter(donostia)

        loadGeoJsonZones()

        // Ocultar panel al tocar fuera de zonas
        map.setOnTouchListener { _, _ ->
            val zoneInfo = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.zoneInfoFragment) as? ZoneInfoFragment
            zoneInfo?.hideZoneInfo()
            false
        }

        return view
    }

    private fun loadGeoJsonZones() {
        try {
            val inputStream = requireContext().assets.open("zonas.geojson")
            val jsonText = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            val json = JSONObject(jsonText)

            val features = json.getJSONArray("features")

            for (i in 0 until features.length()) {
                val feature = features.getJSONObject(i)
                val geometry = feature.getJSONObject("geometry")
                val properties = feature.getJSONObject("properties")

                val zoneName = properties.optString("name", "Zona ${i + 1}")

                val coordinates = geometry.getJSONArray("coordinates").getJSONArray(0)
                val points = mutableListOf<GeoPoint>()
                for (j in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(j)
                    val lon = coord.getDouble(0)
                    val lat = coord.getDouble(1)
                    points.add(GeoPoint(lat, lon))
                }

                val polygon = Polygon(map)
                polygon.points = points
                polygon.fillColor = Color.argb(80, 100, 149, 237) // Azul transparente
                polygon.strokeColor = Color.parseColor("#87CEEB") // Azul cielo
                polygon.strokeWidth = 6.0f

                polygon.setOnClickListener { p, _, _ ->
                    // Restaurar color anterior
                    selectedPolygon?.fillColor = Color.argb(80, 100, 149, 237)

                    // Marcar nuevo seleccionado
                    selectedPolygon = p
                    p.fillColor = Color.argb(120, 255, 255, 255) // Blanco semitransparente

                    // Mostrar panel con información
                    val zoneInfo = requireActivity()
                        .supportFragmentManager
                        .findFragmentById(R.id.zoneInfoFragment) as? ZoneInfoFragment

                    val randomFreeSpots = (5..30).random() // Ejemplo: número aleatorio de sitios
                    zoneInfo?.updateZoneInfo(zoneName, randomFreeSpots)

                    // Hacer zoom y centrar el mapa
                    val center = getPolygonCenter(points)
                    map.controller.animateTo(center)
                    map.controller.setZoom(17.0)

                    map.invalidate()
                    true
                }

                map.overlays.add(polygon)
            }

            map.invalidate()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Calcula el centro de un polígono
    private fun getPolygonCenter(points: List<GeoPoint>): GeoPoint {
        var lat = 0.0
        var lon = 0.0
        for (p in points) {
            lat += p.latitude
            lon += p.longitude
        }
        val total = points.size
        return GeoPoint(lat / total, lon / total)
    }
}
