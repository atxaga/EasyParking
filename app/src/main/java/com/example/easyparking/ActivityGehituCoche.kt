package com.example.easyparking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easyparking.databinding.ActivityGehituCocheBinding
import com.example.easyparking.databinding.ActivityLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

class ActivityGehituCoche : AppCompatActivity() {
    private lateinit var binding: ActivityGehituCocheBinding
    private val db = FirebaseFirestore.getInstance();
    private var userRegistrado: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGehituCocheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userRegistrado = prefs.getString("userRegistrado", null)
        binding.btnGehitu.setOnClickListener { gehituKotxe() }
        binding.btnAtzera.setOnClickListener {
            var intent = Intent(this, CarsActivity::class.java)
            startActivity(intent)
        }
    }
    fun gehituKotxe() {
        val marca = binding.etMarca.text.toString().trim()
        val modelo = binding.etModelo.text.toString().trim()
        val matricula = binding.etMatricula.text.toString().trim().uppercase()

        if (marca.isEmpty() || modelo.isEmpty() || matricula.isEmpty()) {
            binding.mensaje.setTextColor(Color.RED)
            binding.mensaje.text = "Datuak jarri"
            return
        }

        db.collection("coches")
            .whereEqualTo("matricula", matricula)
            .whereEqualTo("user_id", userRegistrado)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Ya existe un coche con esa matrícula para este usuario
                    Toast.makeText(this, "Kotxe hori erregistratuta dago", Toast.LENGTH_SHORT).show()
                } else {
                    // No existe, se puede añadir
                    val kotxeBerria = hashMapOf(
                        "marca" to marca,
                        "modelo" to modelo,
                        "matricula" to matricula,
                        "user_id" to userRegistrado.toString(),
                        "zona" to null
                    )
                    db.collection("coches").add(kotxeBerria)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Kotxe berria gehita!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, CarsActivity::class.java))
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Errorea kotxea gehitzean", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Errorea datuak lortzean", Toast.LENGTH_SHORT).show()
            }
    }


}