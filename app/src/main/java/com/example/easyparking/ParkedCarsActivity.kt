package com.example.easyparking

import Car
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
        db.collection("coches").get().addOnSuccessListener { queryDocumentSnapshots ->
            if(!queryDocumentSnapshots.isEmpty){
                for(document in queryDocumentSnapshots.documents){
                    if(document.getString("user_id").equals(userRegistrado)){
                        if(document.getString("zona") != null) {
                            parkedCars.add(
                                ParkedCar(
                                    document.getString("marca").toString(),
                                    document.getString("modelo").toString(),
                                    document.getString("matricula").toString(),
                                    document.getString("zona").toString(),
                                )
                            )
                        }
                        }
                }
            }
            adapter.notifyDataSetChanged()


        }

    }
}
