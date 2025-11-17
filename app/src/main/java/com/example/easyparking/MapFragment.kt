package com.example.easyparking

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Looper
import android.widget.ImageButton
import org.osmdroid.views.overlay.infowindow.InfoWindow

class MapFragment : Fragment() {

    private lateinit var map: MapView
    private var selectedPolygon: Polygon? = null
    private val firestore = FirebaseFirestore.getInstance()

    // Ubicación real del usuario (se mueve cuando tú caminas)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var myLocationMarker: Marker? = null
    private var myAccuracyCircle: Polyline? = null
    private lateinit var locationCallback: LocationCallback
    private val LOCATION_PERMISSION_CODE = 1001

    // Mira fija en el centro (para apuntar zonas)
    private var fixedCenterMarker: Marker? = null
    private var fixedCenterCircle: Polyline? = null
    private var isFixedModeEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val point = GeoPoint(location.latitude, location.longitude)
                    updateMyLocation(point, location.accuracy)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        map = view.findViewById(R.id.mapView)

        Configuration.getInstance().userAgentValue = requireContext().packageName
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.addMapListener(object : org.osmdroid.events.MapListener {
            override fun onScroll(event: org.osmdroid.events.ScrollEvent): Boolean {
                if (isFixedModeEnabled) {
                    // Actualiza la mira fija cuando mueves el mapa
                    val center = map.mapCenter as GeoPoint
                    fixedCenterMarker?.position = center
                    updateFixedCenterCircle(center, 40f)
                    checkZoneUnderCursor(center)  // ← Detecta zona automáticamente
                }
                return true
            }

            override fun onZoom(event: org.osmdroid.events.ZoomEvent): Boolean {
                if (isFixedModeEnabled) {
                    val center = map.mapCenter as GeoPoint
                    fixedCenterMarker?.position = center
                    updateFixedCenterCircle(center, 40f)
                    checkZoneUnderCursor(center)
                }
                return true
            }
        })
        map.controller.setZoom(18.0)
        map.controller.setCenter(GeoPoint(43.321, -1.985))

        loadSectorsFromFirestore()

        // Botón: activa/desactiva la mira fija
        view.findViewById<ImageButton>(R.id.btnMyLocation)?.setOnClickListener {
            toggleFixedCenterMode()
        }

        return view
    }

    private fun loadSectorsFromFirestore() {
        firestore.collection("sectores")
            .addSnapshotListener { snapshots, error ->
                if (error != null) return@addSnapshotListener

                // Guardamos overlays importantes
                val savedMyMarker = myLocationMarker
                val savedMyCircle = myAccuracyCircle
                val savedFixedMarker = fixedCenterMarker
                val savedFixedCircle = fixedCenterCircle

                map.overlays.clear()

                // Restauramos
                savedMyMarker?.let { map.overlays.add(it) }
                savedMyCircle?.let { map.overlays.add(it) }
                savedFixedMarker?.let { map.overlays.add(it) }
                savedFixedCircle?.let { map.overlays.add(it) }

                snapshots?.forEach { doc ->
                    val sector = doc.toObject(Sector::class.java)?.apply { id = doc.id }
                    if (sector != null) {
                        firestore.collection("coches")
                            .whereEqualTo("zona", sector.nombre)
                            .get()
                            .addOnSuccessListener {
                                sector.libres = sector.capacidad - it.size()
                                drawSectorPolygon(sector)
                            }
                    }
                }
                map.invalidate()
            }
    }

    // === TU CÓDIGO ORIGINAL (drawSectorPolygon, createTextIcon, etc.) ===
    // (lo dejo tal cual, sin tocar)
    private fun drawSectorPolygon(sector: Sector) {
        val points = sector.coordenadas.map { GeoPoint(it.lat, it.lon) }
        val polygon = Polygon(map).apply {
            this.points = points
            fillColor = Color.argb(80, 100, 149, 237)
            strokeColor = Color.parseColor("#87CEEB")
            strokeWidth = 6.0f
        }

        val center = getPolygonCenter(points)

        // GUARDAMOS EL LISTENER CON NUESTRA EXTENSIÓN
        val clickListener = Polygon.OnClickListener { p, _, _ ->
            selectedPolygon?.fillColor = Color.argb(80, 100, 149, 237)
            selectedPolygon = p
            p.fillColor = Color.argb(120, 255, 255, 255)

            val prefs = requireActivity().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().putString("selectedZone", sector.nombre).apply()

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonimo"
            firestore.collection("sectores").document(sector.id).update("usuarioId", userId)

            val coche = hashMapOf("usuarioId" to userId, "zona" to sector.nombre, "timestamp" to System.currentTimeMillis())
            firestore.collection("coches").add(coche)

            val zoneInfo = requireActivity().supportFragmentManager.findFragmentById(R.id.zoneInfoFragment) as? ZoneInfoFragment
            zoneInfo?.updateZoneInfo(sector.nombre, sector.libres)

            map.controller.animateTo(center)
            map.controller.setZoom(17.0)
            map.invalidate()
            true
        }

        polygon.clickListener = clickListener  // ← GUARDAMOS EL LISTENER
        polygon.setOnClickListener(clickListener)  // ← LO ASIGNAMOS

        val label = Marker(map).apply {
            position = center
            icon = createTextIcon(sector.libres.toString())
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }

        map.overlays.add(polygon)
        map.overlays.add(label)
        map.invalidate()
    }

    private fun createTextIcon(text: String): BitmapDrawable {
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
        return BitmapDrawable(resources, bitmap)
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

    // === UBICACIÓN REAL DEL USUARIO (se mueve contigo) ===
    private fun updateMyLocation(geoPoint: GeoPoint, accuracy: Float) {
        // Marcador azul pequeño
        if (myLocationMarker == null) {
            myLocationMarker = Marker(map).apply {
                position = geoPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = createBlueDot()
            }
            map.overlays.add(myLocationMarker)
        } else {
            myLocationMarker?.position = geoPoint
        }

        // Círculo pequeño de precisión
        myAccuracyCircle?.let { map.overlays.remove(it) }
        val radius = accuracy.coerceAtLeast(10f)
        val points = ArrayList<GeoPoint>()
        for (i in 0 until 100) {
            val p = geoPoint.destinationPoint(radius.toDouble(), i * 3.6)
            points.add(p)
        }
        points.add(points[0])

        myAccuracyCircle = Polyline(map).apply {
            isGeodesic = true
            setPoints(points)
            color = Color.argb(50, 66, 133, 244)
            width = 8f
        }
        map.overlays.add(myAccuracyCircle)

        map.invalidate()
    }

    // === MIRA FIJA EN EL CENTRO (se mueve cuando arrastras el mapa) ===
    private fun toggleFixedCenterMode() {
        isFixedModeEnabled = !isFixedModeEnabled

        if (isFixedModeEnabled) {
            val center = map.mapCenter as GeoPoint

            // Crear marcador fijo
            fixedCenterMarker = Marker(map).apply {
                position = center
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_mylocation)
                    ?.apply { setTint(Color.parseColor("#4285F4")) }
                    ?: createBlueDot()
            }
            map.overlays.add(fixedCenterMarker)

            // Crear círculo fijo
            fixedCenterCircle = Polyline(map).apply {
                isGeodesic = true
                color = Color.argb(80, 66, 133, 244)
                width = 12f
            }
            updateFixedCenterCircle(center, 40f)
            map.overlays.add(fixedCenterCircle)

            // Cambiar botón a azul
            view?.findViewById<ImageButton>(R.id.btnMyLocation)
                ?.setBackgroundColor(Color.parseColor("#4285F4"))

            // Comprobar zona al activar
            checkZoneUnderCursor(center)

        } else {
            // Desactivar
            fixedCenterMarker?.let { map.overlays.remove(it) }
            fixedCenterCircle?.let { map.overlays.remove(it) }
            fixedCenterMarker = null
            fixedCenterCircle = null

            view?.findViewById<ImageButton>(R.id.btnMyLocation)
                ?.setBackgroundColor(Color.TRANSPARENT)
        }

        map.invalidate()
    }

    private fun checkZoneUnderCursor(center: GeoPoint) {
        for (overlay in map.overlays) {
            if (overlay is Polygon && overlay.clickListener != null) {
                if (isPointInPolygon(center, overlay.points)) {
                    overlay.clickListener?.onClick(overlay, map, null)
                    return
                }
            }
        }
    }

    private fun isPointInPolygon(point: GeoPoint, polygonPoints: List<GeoPoint>): Boolean {
        var inside = false
        var j = polygonPoints.size - 1
        for (i in polygonPoints.indices) {
            val pi = polygonPoints[i]
            val pj = polygonPoints[j]

            if (((pi.longitude > point.longitude) != (pj.longitude > point.longitude)) &&
                (point.latitude < (pj.latitude - pi.latitude) * (point.longitude - pi.longitude) /
                        (pj.longitude - pi.longitude) + pi.latitude)) {
                inside = !inside
            }
            j = i
        }
        return inside
    }

    private fun updateFixedCenterCircle(center: GeoPoint, radiusMeters: Float) {
        val points = ArrayList<GeoPoint>()
        for (i in 0 until 100) {
            val p = center.destinationPoint(radiusMeters.toDouble(), i * 3.6)
            points.add(p)
        }
        points.add(points[0])
        fixedCenterCircle?.setPoints(points)
    }

    // === PERMISOS Y ACTUALIZACIONES GPS ===
    private fun checkLocationPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        // Primera ubicación rápida
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val point = GeoPoint(it.latitude, it.longitude)
                map.controller.animateTo(point)
                updateMyLocation(point, it.accuracy)
            }
        }

        // Actualizaciones continuas cada 3-5 segundos
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    private fun createBlueDot(): BitmapDrawable {
        val size = 64
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4285F4")
            style = Paint.Style.FILL
            canvas.drawCircle(size / 2f, size / 2f, 20f, this)
        }
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            strokeWidth = 7f
            style = Paint.Style.STROKE
            canvas.drawCircle(size / 2f, size / 2f, 20f, this)
        }
        return BitmapDrawable(resources, bitmap)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    private var Polygon.clickListener: Polygon.OnClickListener?
        get() = getInfoWindow() as? Polygon.OnClickListener
        set(value) { infoWindow = value as? InfoWindow }
}


