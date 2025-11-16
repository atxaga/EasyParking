package com.example.easyparking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easyparking.databinding.ActivityLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Comprobar si ya hay un usuario guardado
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val savedUser = prefs.getString("userRegistrado", null)

        if (savedUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener { iniciarSesion() }
        binding.registerText.setOnClickListener { abrirRegistro() }
    }

    private fun iniciarSesion() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("usuarios")
            .whereEqualTo("email", email)
            .whereEqualTo("contraseña", password)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                } else {
                    val userDoc = result.documents.first()
                    val userId = userDoc.id  // <-- ESTE ES EL UID REAL

                    // Guardar usuario logueado
                    val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    prefs.edit().putString("userRegistrado", userId).apply()

                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }
    }

    private fun abrirRegistro() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}
