package com.example.easyparking

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

data class Coordenada(
    val lat: Double,
    val lon: Double
){
    constructor() : this(0.0, 0.0) // Constructor vacío requerido por Firestore
}

data class Plaza(
    val ocupado: Boolean = false,
    val tiempoRestante: Int = 0,
    val usuarioId: Int? = null
)

data class Sector(
    var id: String = "",
    val nombre: String = "",
    val capacidad: Int = 0,
    val libres: Int = 0,
    val plazas: List<Plaza> = emptyList(),
    val coordenadas: List<Coordenada> = emptyList()
)

fun inicializarZonasDesdeJson(context: Context) {
    val db = FirebaseFirestore.getInstance()

    try {
        // 🔹 Leer el JSON desde assets/zonas.geojson
        val inputStream = context.assets.open("zonas.geojson")
        val jsonText = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonText)
        val features = jsonObject.getJSONArray("features")

        // 🔹 Primero borramos todo lo que haya en la colección
        db.collection("sectores").get().addOnSuccessListener { snapshot ->
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().addOnSuccessListener {
                Log.d("Firestore", "🗑️ Colección 'sectores' vaciada, subiendo zonas...")

                // 🔹 Ahora subimos todas las zonas del JSON
                for (i in 0 until features.length()) {
                    val feature = features.getJSONObject(i)
                    val properties = feature.getJSONObject("properties")
                    val geometry = feature.getJSONObject("geometry")

                    val nombre = properties.optString("nombre", "SinNombre")
                    val coordenadas = geometry.getJSONArray("coordinates")

                    // 🔸 Convertir coordenadas a List<Coordenada>
                    val coordsList = mutableListOf<Coordenada>()
                    val firstRing = coordenadas.getJSONArray(0)
                    for (k in 0 until firstRing.length()) {
                        val pair = firstRing.getJSONArray(k)
                        coordsList.add(Coordenada(lat = pair.getDouble(1), lon = pair.getDouble(0)))
                    }

                    // 🔹 Calcular capacidad según área
                    val capacidad = calcularCapacidad(coordsList)

                    // 🔹 Crear plazas vacías
                    val plazas = List(capacidad) { Plaza(ocupado = false) }

                    // 🔹 Crear sector con capacidad calculada
                    val sector = Sector(
                        nombre = nombre,
                        capacidad = capacidad,
                        libres = capacidad,
                        plazas = plazas,
                        coordenadas = coordsList
                    )

                    // 🔹 Subir sector a Firestore
                    db.collection("sectores").document(nombre)
                        .set(sector)
                        .addOnSuccessListener { Log.d("Firestore", "✅ Zona '$nombre' añadida") }
                        .addOnFailureListener { e -> Log.e("Firestore", "❌ Error al subir '$nombre'", e) }
                }

            }.addOnFailureListener { e ->
                Log.e("FirestoreInit", "❌ Error vaciando la colección", e)
            }
        }.addOnFailureListener { e ->
            Log.e("FirestoreInit", "❌ Error obteniendo documentos para borrar", e)
        }

    } catch (e: Exception) {
        Log.e("FirestoreInit", "❌ Error al leer JSON", e)
    }
}

fun calcularCapacidad(coordenadas: List<Coordenada>): Int {
    // Aproximación de área usando fórmula del polígono
    val n = coordenadas.size
    var area = 0.0
    for (i in 0 until n) {
        val j = (i + 1) % n
        area += coordenadas[i].lon * coordenadas[j].lat
        area -= coordenadas[j].lon * coordenadas[i].lat
    }
    area = kotlin.math.abs(area) / 2.0

    // Mapear área a capacidad entre 300 y 1000
    val capacidad = (300 + area * 50000).toInt()
    return capacidad.coerceIn(300, 1000)
}
