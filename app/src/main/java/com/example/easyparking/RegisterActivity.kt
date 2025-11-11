package com.example.easyparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class RegisterActivity : AppCompatActivity() {

    private lateinit var loginButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        loginButton = findViewById(R.id.registerText)

        loginButton.setOnClickListener { login() }
    }
    fun login(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}
