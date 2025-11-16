package com.example.easyparking

import Car
import CarAdapter
import ParkedCar
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyparking.databinding.ActivityCarsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CarsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarsBinding
    private lateinit var carAdapter: CarAdapter
    private val carList = mutableListOf<Car>()
    private val db = FirebaseFirestore.getInstance();
    private var userRegistrado: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("DEBUG", "UID actual: ${FirebaseAuth.getInstance().currentUser?.uid}")

        binding.carsRecyclerView.layoutManager = LinearLayoutManager(this)
        carAdapter = CarAdapter(carList)
        binding.carsRecyclerView.adapter = carAdapter

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userRegistrado = prefs.getString("userRegistrado", null)

        loadCarsFromDatabase()

        binding.addCarButton.setOnClickListener {
            var intent = Intent(this, ActivityGehituCoche::class.java)
            startActivity(intent)
        }

    }

    private fun loadCarsFromDatabase() {
        var hayCoche = false
        db.collection("coches").get().addOnSuccessListener { queryDocumentSnapshots ->
            if(!queryDocumentSnapshots.isEmpty){
                for(document in queryDocumentSnapshots.documents){
                    if(document.getString("user_id").equals(userRegistrado)){
                        var marca = document.getString("marca")
                        if(marca != null){hayCoche=true}
                        carList.add(Car(document.getString("marca").toString(), document.getString("modelo").toString(), document.getString("matricula").toString(), document.id ))
                    }
                }
                if(!hayCoche){
                    binding.noCarsLayout.visibility = View.VISIBLE
                }
            }
            carAdapter.notifyDataSetChanged()

        }

    }
    fun borratuKotxe(documentId: String) {
        db.collection("coches")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Coche borrado", Toast.LENGTH_SHORT).show()

                // borrar de la lista
                val iterator = carList.iterator()
                while (iterator.hasNext()) {
                    val car = iterator.next()
                    if (car.user_id == documentId) {
                        iterator.remove()
                    }
                }

                carAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al borrar", Toast.LENGTH_SHORT).show()
            }
    }


}
