package com.example.easyparking

import Car
import CarAdapter
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CarsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var carAdapter: CarAdapter
    private val carList = mutableListOf<Car>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cars)

        recyclerView = findViewById(R.id.carsRecyclerView)
        addButton = findViewById(R.id.addCarButton)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        carAdapter = CarAdapter(carList)
        recyclerView.adapter = carAdapter

        // Cargar coches desde la base de datos
        loadCarsFromDatabase()

        // Acción del botón
        addButton.setOnClickListener {
            // Abrir un formulario para añadir coche
        }
    }

    private fun loadCarsFromDatabase() {
        // Ejemplo de datos simulados, reemplaza con tu BD
        carList.add(Car("Toyota", "Corolla", "1234ABC"))
        carList.add(Car("Ford", "Focus", "5678DEF"))
        carList.add(Car("Honda", "Civic", "9012GHI"))

        carAdapter.notifyDataSetChanged()
    }
}
