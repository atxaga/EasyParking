package com.example.easyparking

import Car
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyparking.databinding.FragmentAparkatuBinding
import com.google.firebase.firestore.FirebaseFirestore

class AparkatuFragment : Fragment() {

    private var _binding: FragmentAparkatuBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CarSelectAdapter
    private val carList = mutableListOf<Car>()

    private val db = FirebaseFirestore.getInstance()
    private var selectedCar: Car? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAparkatuBinding.inflate(inflater, container, false)

        setupRecycler()
        loadCars()

        binding.btnAparkatu.setOnClickListener { aparkatu() }
        binding.btnAtzera.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        return binding.root
    }

    private fun setupRecycler() {
        adapter = CarSelectAdapter(carList) { car ->
            selectedCar = car
            Toast.makeText(requireContext(), "Seleccionado: ${car.marca} ${car.modelo}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerCars.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCars.adapter = adapter
    }

    private fun loadCars() {
        val prefs = requireActivity().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val uid = prefs.getString("userRegistrado", null)

        if (uid == null) {
            Toast.makeText(requireContext(), "Error: usuario no logueado", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("coches")
            .whereEqualTo("user_id", uid)
            .whereEqualTo("zona", null)
            .get()
            .addOnSuccessListener { result ->
                carList.clear()
                for (doc in result) {
                    val car = Car(
                        marca = doc.getString("marca") ?: "",
                        modelo = doc.getString("modelo") ?: "",
                        matricula = doc.getString("matricula") ?: ""
                    )
                    carList.add(car)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error cargando coches", Toast.LENGTH_SHORT).show()
            }
    }

    private fun aparkatu() {
        val prefs = requireActivity().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val userId = prefs.getString("userRegistrado", null)
        val selectedZone = prefs.getString("selectedZone", null)

        if (selectedCar == null) {
            Toast.makeText(requireContext(), "Selecciona un coche", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedZone == null) {
            Toast.makeText(requireContext(), "Ez duzu zonarik hautatu", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("coches")
            .whereEqualTo("user_id", userId)
            .whereEqualTo("matricula", selectedCar!!.matricula)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val carDocId = result.documents[0].id

                    db.collection("coches")
                        .document(carDocId)
                        .update("zona", selectedZone)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Autoa aparkatuta!", Toast.LENGTH_SHORT).show()
                        }
                    db.collection("sectores").whereEqualTo("nombre", selectedZone).get().addOnSuccessListener { queryDocumentSnapshots ->
                        for(document in queryDocumentSnapshots.documents){
                            val capacidad = document.getLong("capacidad")?.toInt() ?: 0
                            var capacidadBerria = capacidad-1
                            db.collection("sectores").document(document.id).update("capacidad", capacidadBerria)
                        }
                    }
                    var intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }

            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
