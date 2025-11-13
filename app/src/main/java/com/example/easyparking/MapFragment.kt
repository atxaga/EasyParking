package com.example.easyparking

import android.graphics.*
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
import org.osmdroid.views.overlay.Marker
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
                polygon.fillColor = Color.argb(80, 100, 149, 237)
                polygon.strokeColor = Color.parseColor("#87CEEB")
                polygon.strokeWidth = 6.0f

                val center = getPolygonCenter(points)
                val randomFreeSpots = (5..30).random()

                // ✅ Crear marcador con texto dibujado
                val label = Marker(map)
                label.position = center
                label.icon = createTextIcon(randomFreeSpots.toString())
                label.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

                polygon.setOnClickListener { p, _, _ ->
                    selectedPolygon?.fillColor = Color.argb(80, 100, 149, 237)
                    selectedPolygon = p
                    p.fillColor = Color.argb(120, 255, 255, 255)

                    val zoneInfo = requireActivity()
                        .supportFragmentManager
                        .findFragmentById(R.id.zoneInfoFragment) as? ZoneInfoFragment
                    zoneInfo?.updateZoneInfo(zoneName, randomFreeSpots)

                    map.controller.animateTo(center)
                    map.controller.setZoom(17.0)
                    map.invalidate()
                    true
                }

                map.overlays.add(polygon)
                map.overlays.add(label)
            }

            map.invalidate()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 🟢 Dibuja el número como imagen para usar como icono
    private fun createTextIcon(text: String): android.graphics.drawable.BitmapDrawable {
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 42f // antes 70f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(6f, 0f, 0f, Color.argb(150, 0, 0, 0))
            isAntiAlias = true
        }

        val backgroundPaint = Paint().apply {
            color = Color.parseColor("#1ABC9C") // verde agua moderno
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val padding = 20 // antes 40
        val textWidth = textPaint.measureText(text)
        val textHeight = textPaint.descent() - textPaint.ascent()
        val width = (textWidth + padding * 2).toInt()
        val height = (textHeight + padding * 2).toInt()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, 30f, 30f, backgroundPaint)

        val xPos = width / 2f
        val yPos = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, xPos, yPos, textPaint)

        return android.graphics.drawable.BitmapDrawable(resources, bitmap)
    }



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
