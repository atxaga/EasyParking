package com.example.easyparking

import ParkedCar
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyparking.databinding.ActivityParkedCarsBinding
import com.google.firebase.firestore.FirebaseFirestore

class ParkedCarsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParkedCarsBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParkedCarsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.parkedCarsRecyclerView.layoutManager = LinearLayoutManager(this)

        cargarCochesAparcados()
    }

    private fun cargarCochesAparcados() {

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = prefs.getString("userRegistrado", null)

        if (userId == null) {
            mostrarSinCoches()
            return
        }

        db.collection("coches")
            .whereEqualTo("user_id", userId)
            .whereNotEqualTo("zona", null)  // SOLO coches aparcados
            .get()
            .addOnSuccessListener { result ->

                if (result.isEmpty) {
                    mostrarSinCoches()
                    return@addOnSuccessListener
                }

                val lista = result.documents.map { doc ->
                    ParkedCar(
                        marca = doc.getString("marca") ?: "",
                        modelo = doc.getString("modelo") ?: "",
                        matricula = doc.getString("matricula") ?: "",
                        zone = doc.getString("zona") ?: "Desconocida"
                    )
                }

                binding.noCarsLayout.visibility = View.GONE
                binding.parkedCarsRecyclerView.visibility = View.VISIBLE
                binding.parkedCarsRecyclerView.adapter = ParkedCarAdapter(lista)
            }
            .addOnFailureListener {
                mostrarSinCoches()
            }
    }

    private fun mostrarSinCoches() {
        binding.noCarsLayout.visibility = View.VISIBLE
        binding.parkedCarsRecyclerView.visibility = View.GONE
    }
}
