package com.example.easyparking

import Car
import ParkedCar
import com.example.easyparking.R
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easyparking.databinding.ActivityParkedCarsBinding
import com.google.firebase.firestore.FirebaseFirestore

class ParkedCarsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: Button
    private val parkedCars = mutableListOf<ParkedCar>()
    private lateinit var adapter: ParkedCarAdapter
    private val db = FirebaseFirestore.getInstance();
    private var userRegistrado: String? = null
    private lateinit var binding: ActivityParkedCarsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityParkedCarsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recyclerView = findViewById(R.id.parkedCarsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ParkedCarAdapter(parkedCars)
        recyclerView.adapter = adapter

        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userRegistrado = prefs.getString("userRegistrado", null)

        loadParkedCarsFromDatabase()


    }

    private fun loadParkedCarsFromDatabase() {
        var hayCoche = false
        db.collection("coches").get().addOnSuccessListener { queryDocumentSnapshots ->
            if(!queryDocumentSnapshots.isEmpty){
                for(document in queryDocumentSnapshots.documents){
                    if(document.getString("user_id").equals(userRegistrado)){
                        if(document.getString("zona") != null) {
                            var marca = document.getString("marca")
                            if(marca != null){hayCoche = true}
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
                if(!hayCoche){
                    binding.noCarsLayout.visibility = View.VISIBLE
                }

            }
            adapter.notifyDataSetChanged()


        }

    }
}
