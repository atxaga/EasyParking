package com.example.easyparking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.easyparking.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listeners de botones
        binding.loginButton.setOnClickListener { saioaHasi() }
        binding.registerText.setOnClickListener { register() }
    }

    private fun saioaHasi() {
        val user = binding.emailEditText.text.toString()
        val pasahitza = binding.passwordEditText.text.toString()

        if (user == "bittor" && pasahitza == "bittor") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun register() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
