package com.example.easyparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class LoginActivity : AppCompatActivity() {

    private lateinit var user: EditText
    private lateinit var pasahitza: EditText
    private lateinit var saioaHasi: Button

    private lateinit var registerButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        user = findViewById(R.id.emailEditText)
        pasahitza = findViewById(R.id.passwordEditText)
        saioaHasi = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerText)

        saioaHasi.setOnClickListener {saioaHasi() }
        registerButton.setOnClickListener { register() }

    }
    fun saioaHasi(){
        if (user.text.toString() == "bittor" && pasahitza.text.toString() == "bittor") {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    fun register(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)

    }
}
