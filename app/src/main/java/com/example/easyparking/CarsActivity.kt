package com.example.easyparking

import Car
import CarAdapter
import ParkedCar
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyparking.databinding.ActivityCarsBinding
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

        binding.carsRecyclerView.layoutManager = LinearLayoutManager(this)
        carAdapter = CarAdapter(carList)
        binding.carsRecyclerView.adapter = carAdapter

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userRegistrado = prefs.getString("userRegistrado", null)

        loadCarsFromDatabase()

        binding.addCarButton.setOnClickListener {
        }
    }

    private fun loadCarsFromDatabase() {
        db.collection("coches").get().addOnSuccessListener { queryDocumentSnapshots ->
            if(!queryDocumentSnapshots.isEmpty){
                for(document in queryDocumentSnapshots.documents){
                    if(document.getString("user_id").equals(userRegistrado)){
                        carList.add(Car(document.getString("marca").toString(), document.getString("matricula").toString(), document.getString("modelo").toString()))
                    }
                }
            }
            carAdapter.notifyDataSetChanged()

        }

    }
}
