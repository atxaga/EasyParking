package com.example.easyparking

import Car
import CarAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyparking.databinding.ActivityCarsBinding

class CarsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarsBinding
    private lateinit var carAdapter: CarAdapter
    private val carList = mutableListOf<Car>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView
        binding.carsRecyclerView.layoutManager = LinearLayoutManager(this)
        carAdapter = CarAdapter(carList)
        binding.carsRecyclerView.adapter = carAdapter

        // Cargar coches desde la base de datos
        loadCarsFromDatabase()

        // Acción del botón
        binding.addCarButton.setOnClickListener {
            // TODO: Abrir un formulario para añadir coche
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
