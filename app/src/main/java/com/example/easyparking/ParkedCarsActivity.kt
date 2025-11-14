package com.example.easyparking

import ParkedCar
import com.example.easyparking.R
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ParkedCarsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: Button
    private val parkedCars = mutableListOf<ParkedCar>()
    private lateinit var adapter: ParkedCarAdapter
    private val db = FirebaseFirestore.getInstance();
    private var userRegistrado: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parked_cars)

        recyclerView = findViewById(R.id.parkedCarsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ParkedCarAdapter(parkedCars)
        recyclerView.adapter = adapter

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userRegistrado = prefs.getString("userRegistrado", null)

        loadParkedCarsFromDatabase()


    }

    private fun loadParkedCarsFromDatabase() {

        // Datos simulados
        parkedCars.add(ParkedCar("Toyota", "Corolla", "1234ABC", "A1"))
        parkedCars.add(ParkedCar("Ford", "Focus", "5678DEF", "B2"))
        parkedCars.add(ParkedCar("Honda", "Civic", "9012GHI", "C3"))

    }
}
