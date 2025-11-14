package com.example.easyparking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easyparking.databinding.RegisterActivityBinding
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisterActivityBinding
    private val db = FirebaseFirestore.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerText.setOnClickListener { login() }
        binding.loginButton.setOnClickListener { kontuaSortu() }
    }
    private fun kontuaSortu(){
        var existitzenDa = true
        var contraseñaRepe = binding.passwordEditTextErrepikatu.text.toString();
        var contraseña = binding.passwordEditText.text.toString();
        var email = binding.emailEditText.text.toString();

        db.collection("usuarios").get().addOnSuccessListener { queryDocumentSnapshots ->
            if(!queryDocumentSnapshots.isEmpty){
                for (document in queryDocumentSnapshots.documents){
                    if(document.getString("nombre").equals(binding.registerText.text.toString())){
                        Toast.makeText(this, "Email hau existitzen da", Toast.LENGTH_SHORT).show()
                    }else if(contraseña != contraseñaRepe){
                        Toast.makeText(this, "Pasahitz berdina jarri", Toast.LENGTH_SHORT).show()

                    }else if(email.isEmpty() && contraseña.isEmpty() && email.isEmpty()){
                        Toast.makeText(this, "Datuak jarri", Toast.LENGTH_SHORT).show()

                    }else{
                        existitzenDa = false
                    }
                }
            }
            if(!existitzenDa){
                var erabiltzailea = hashMapOf(
                    "contraseña" to contraseña,
                    "email" to email
                )
                db.collection("usuarios").add(erabiltzailea);

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
