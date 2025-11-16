package com.example.easyparking

import Car
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.easyparking.databinding.ItemCarBinding

class CarSelectAdapter(
    private val cars: List<Car>,
    private val onCarSelected: (Car) -> Unit
) : RecyclerView.Adapter<CarSelectAdapter.CarViewHolder>() {

    inner class CarViewHolder(val binding: ItemCarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun getItemCount(): Int = cars.size

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]

        holder.binding.brandText.text = car.marca
        holder.binding.modelText.text = car.modelo
        holder.binding.plateText.text = car.matricula

        holder.itemView.setOnClickListener {
            onCarSelected(car)
        }
    }
}
