package com.example.easyparking

import ParkedCar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.easyparking.databinding.ItemParkedCarBinding

class ParkedCarAdapter(private val cars: List<ParkedCar>) :
    RecyclerView.Adapter<ParkedCarAdapter.ParkedCarViewHolder>() {

    inner class ParkedCarViewHolder(val binding: ItemParkedCarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkedCarViewHolder {
        val binding = ItemParkedCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParkedCarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParkedCarViewHolder, position: Int) {
        val car = cars[position]
        with(holder.binding) {
            brandText.text = car.marca
            modelText.text = car.modelo
            plateText.text = car.matricula
            zoneText.text = "Zona: ${car.zone}"
            carIcon.setImageResource(R.drawable.coche)
        }
    }

    override fun getItemCount(): Int = cars.size
}
