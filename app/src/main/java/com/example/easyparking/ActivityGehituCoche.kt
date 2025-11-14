package com.example.easyparking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
    fun gehituKotxe(){
        if(binding.etMarca.text.toString().isEmpty() || binding.etModelo.toString().isEmpty() || binding.etMatricula.toString().isEmpty()){
            binding.mensaje.setTextColor(Color.RED);
            binding.mensaje.text = "Datuak jarri"
        }else{
            var kotxeBerria = hashMapOf(
                "marca" to binding.etMarca.text.toString(),
                "modelo" to binding.etModelo.text.toString(),
                "matricula" to binding.etMatricula.text.toString().toUpperCase(),
                "user_id" to userRegistrado.toString(),
                "zona" to null
            )
            db.collection("coches").add(kotxeBerria).addOnSuccessListener {
                val intent = Intent(this, CarsActivity::class.java)
                startActivity(intent)
            }
        }

    }

}