package com.example.easyparking

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easyparking.databinding.ActivityCarsBinding
import com.example.easyparking.databinding.ActivityCuentaBinding
import com.example.easyparking.databinding.FragmentCuentaBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Color

class ActivityCuenta : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityCuentaBinding
    private var userRegistrado: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userRegistrado = prefs.getString("userRegistrado", null)

        binding.logoutButton.setOnClickListener { logout() }
        binding.saveButton.setOnClickListener { gordeAldaketak() }
        loadDatuak()



    }

    fun gordeAldaketak(){
        val emailBerria = binding.emailEditText.text.toString().trim()
        val pasahitzBerria = binding.passwordEditText.text.toString().trim()
        val updates = mutableMapOf<String, Any>()
        if (emailBerria.isNotEmpty()) updates["email"] = emailBerria
        if (pasahitzBerria.isNotEmpty()) updates["contraseña"] = pasahitzBerria

        val documentid: String = userRegistrado.toString()
        db.collection("usuarios").document(documentid)
            .update(updates)
            .addOnSuccessListener {
                binding.mensaje.text = "Datuak aldatuak"
                loadDatuak()
            }
            .addOnFailureListener { e ->
                binding.mensaje.setTextColor(Color.RED)

                binding.mensaje.text = "Errorea datuak aldatzen"
            }



    }
    fun logout(){
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        prefs.edit().remove("userRegistrado").apply()
        userRegistrado = null;
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    fun loadDatuak(){

        db.collection("usuarios").get().addOnSuccessListener { queryDocumentSnapshots ->
            for(document in queryDocumentSnapshots.documents){
                if(document.id == userRegistrado){
                    binding.emailText.text = document.getString("email").toString()
                    binding.emailEditText.setText(document.getString("email"))
                }
            }
        }
    }
}
