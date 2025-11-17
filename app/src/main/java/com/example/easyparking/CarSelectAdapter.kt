package com.example.easyparking

import Car
import android.R
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.easyparking.databinding.ItemCarBinding

class CarSelectAdapter(
    private val cars: List<Car>,
    private val onCarSelected: (Car) -> Unit
) : RecyclerView.Adapter<CarSelectAdapter.CarViewHolder>() {

    private var selectedPosition = -1 // Para saber qué coche está seleccionado

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

        if (position == selectedPosition) {
            holder.binding.carLayout.setBackgroundColor(Color.CYAN)
            holder.binding.brandText.setTextColor(Color.WHITE)
            holder.binding.modelText.setTextColor(Color.WHITE)
            holder.binding.plateText.setTextColor(Color.WHITE)
      } else {
            holder.binding.carLayout.setBackgroundColor(Color.WHITE)
            holder.binding.brandText.setTextColor(Color.BLACK)
            holder.binding.modelText.setTextColor(Color.DKGRAY)
            holder.binding.plateText.setTextColor(Color.GRAY)
    }

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelected) // refresca el anterior seleccionado
            notifyItemChanged(selectedPosition) // refresca el nuevo seleccionado
            onCarSelected(car)
        }
    }
}
