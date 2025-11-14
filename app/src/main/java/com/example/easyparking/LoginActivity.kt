package com.example.easyparking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easyparking.databinding.ActivityLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance();
    private var userRegistrado: String?=null

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { saioaHasi() }
        binding.registerText.setOnClickListener { register() }

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userRegistrado = prefs.getString("userRegistrado", null)

        if(userRegistrado != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

    private fun saioaHasi() {
        val user = binding.emailEditText.text.toString()
        val pasahitza = binding.passwordEditText.text.toString()
        var kredentzalak = false;

        db.collection("usuarios").get().addOnSuccessListener { queryDocumentSnapshots ->
            if(!queryDocumentSnapshots.isEmpty){
                for (document in queryDocumentSnapshots.documents){
                    if(document.getString("email").equals(user) && document.getString("contraseña").equals(pasahitza)){
                        kredentzalak= true;
                        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        prefs.edit().putString("userRegistrado", document.id).apply()
                        break


                    }
                }
                if (!kredentzalak){
                    Toast.makeText(this, "Kredentzial okerrak", Toast.LENGTH_SHORT).show()
                }else{
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }


    }

    private fun register() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
