package com.example.easyparking

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

class MapFragment : Fragment() {

    private lateinit var map: MapView
    private var selectedPolygon: Polygon? = null
    private val firestore = FirebaseFirestore.getInstance()

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

        loadSectorsFromFirestore()

        map.setOnTouchListener { _, _ ->
            val zoneInfo = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.zoneInfoFragment) as? ZoneInfoFragment
            zoneInfo?.hideZoneInfo()
            false


        }

        return view
    }

    private fun loadSectorsFromFirestore() {
        firestore.collection("sectores")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("MapFragment", "Error cargando sectores: ${error.message}")
                    return@addSnapshotListener
                }

                // Limpiamos los polígonos anteriores
                map.overlays.clear()

                snapshots?.forEach { doc ->
                    val sector = doc.toObject(Sector::class.java)?.apply { id = doc.id }

                    // Calcula los libres: capacidad menos coches aparcados en este sector
                    if (sector != null) {
                        firestore.collection("coches")
                            .whereEqualTo("zona", sector.nombre)
                            .get()
                            .addOnSuccessListener { cochesSnapshot ->
                                sector.libres = sector.capacidad
                                drawSectorPolygon(sector)
                            }
                    }
                }

                map.invalidate()
            }
    }



    private fun drawSectorPolygon(sector: Sector) {
        val points = sector.coordenadas.map { GeoPoint(it.lat, it.lon) }

        val polygon = Polygon(map)
        polygon.points = points
        polygon.fillColor = Color.argb(80, 100, 149, 237)
        polygon.strokeColor = Color.parseColor("#87CEEB")
        polygon.strokeWidth = 6.0f

        val center = getPolygonCenter(points)

        // Marcador con el NOMBRE del sector
        val label = Marker(map)
        label.position = center
        label.icon = createTextIcon(sector.libres.toString()) // usamos libres actualizados
        label.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        polygon.setOnClickListener { p, _, _ ->
            // Resaltar zona seleccionada
            selectedPolygon?.fillColor = Color.argb(80, 100, 149, 237)
            selectedPolygon = p
            p.fillColor = Color.argb(120, 255, 255, 255)

            // Guardamos la zona seleccionada en SharedPreferences
            val prefs = requireActivity().getSharedPreferences(
                "UserPrefs",
                android.content.Context.MODE_PRIVATE
            )
            prefs.edit().putString("selectedZone", sector.nombre).apply()

            // Actualizamos Firestore: sector.usuarioId
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonimo" // FirebaseAuth.getInstance().currentUser?.uid
            firestore.collection("sectores").document(sector.id)
                .update("usuarioId", userId)
                .addOnSuccessListener {
                    Log.d("MapFragment", "Sector actualizado con usuarioId")
                }
                .addOnFailureListener { e ->
                    Log.e("MapFragment", "Error al actualizar sector: ${e.message}")
                }

            // Guardar en tabla "coches" (aparcar)
            val coche = hashMapOf(
                "usuarioId" to userId,
                "zona" to sector.nombre,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("coches")
                .add(coche)
                .addOnSuccessListener { Log.d("MapFragment", "Coche aparcado") }
                .addOnFailureListener { e -> Log.e("MapFragment", "Error al aparcar: ${e.message}") }

            // Mostrar info de la zona
            val zoneInfo = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.zoneInfoFragment) as? ZoneInfoFragment
            zoneInfo?.updateZoneInfo(sector.nombre, sector.libres)

            map.controller.animateTo(center)
            map.controller.setZoom(17.0)
            map.invalidate()
            true
        }

        map.overlays.add(polygon)
        map.overlays.add(label)
        map.invalidate()
    }


    // Dibuja el número como imagen para usar como icono
    private fun createTextIcon(text: String): android.graphics.drawable.BitmapDrawable {
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 42f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(6f, 0f, 0f, Color.argb(150, 0, 0, 0))
            isAntiAlias = true
        }

        val backgroundPaint = Paint().apply {
            color = Color.parseColor("#1ABC9C")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val padding = 20
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
        return GeoPoint(lat / points.size, lon / points.size)
    }
}

