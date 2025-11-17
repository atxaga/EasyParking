package com.example.easyparking

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import android.widget.FrameLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cargamos los fragmentos solo la primera vez
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)

                // Fragmento del mapa
                replace(R.id.mapFragmentContainer, MapFragment())

            }
        }
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        currentUserId = prefs.getString("userRegistrado", null)

    checkUserParking()
    }
    private fun checkUserParking() {
        if (currentUserId == null) return

        firestore.collection("coches")
            .whereEqualTo("user_id", currentUserId)
            .get()
            .addOnSuccessListener { result ->

                if (!result.isEmpty) {
                    for (document in result.documents) {
                        if(document.getString("zona") != null) {

                            // Tomamos el primer coche aparcado
                            val zone = document.getString("zona")?: "De"
                            val car = " ${document.getString("marca")} ${document.getString("matricula")} " ?: "Coche desconocido"

                            showParkingInfo(car, zone)

                        }


                        // Mostramos el fragmento con info del coche
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error comprobando coches: ${e.message}")
                hideParkingInfoFragment()
            }
    }

    private fun showParkingInfo(car: String, zone: String) {
        val parkingInfo = findViewById<FrameLayout>(R.id.parkingInfo)
        parkingInfo.visibility = View.VISIBLE

        val zoneName = parkingInfo.findViewById<TextView>(R.id.zoneName)
        val carInfo = parkingInfo.findViewById<TextView>(R.id.carInfo)

        zoneName.text = zone
        carInfo.text = car
    }

    private fun hideParkingInfo() {
        findViewById<FrameLayout>(R.id.parkingInfo).visibility = View.GONE
    }


    private fun hideParkingInfoFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.parkingInfo) as? ZoneInfoFragment
        fragment?.view?.visibility = android.view.View.GONE
    }




}
