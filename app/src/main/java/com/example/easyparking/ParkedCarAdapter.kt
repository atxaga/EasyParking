package com.example.easyparking

import ParkedCar
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.easyparking.databinding.ItemParkedCarBinding
import com.google.firebase.firestore.FirebaseFirestore

class ParkedCarAdapter(
    private val cars: List<ParkedCar>,
    private val userRegistrado: String?
) : RecyclerView.Adapter<ParkedCarAdapter.ParkedCarViewHolder>() {

        private val db = FirebaseFirestore.getInstance()


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

            moreIcon.setOnClickListener {
                val context = root.context

                com.google.android.material.dialog.MaterialAlertDialogBuilder(context)
                    .setTitle("Ziur zaude?")
                    .setMessage("Kotxe hau aparkalekutik atera duzu: ${car.matricula}?")
                    .setIcon(R.drawable.ic_basura) // icono de basura bonito
                    .setPositiveButton("Bai") { dialog, _ ->
                        borratuAparkalekua(car.matricula, position)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Ez") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }
    fun borratuAparkalekua(matricula: String, position: Int) {
        var zona: String? = null
        db.collection("coches")
            .whereEqualTo("user_id", userRegistrado)
            .whereEqualTo("matricula", matricula)
            .get()
            .addOnSuccessListener { query ->
                for (document in query.documents) {
                    zona = document.getString("zona");
                    document.reference.update("zona", null)
                        .addOnSuccessListener {
                            (cars as MutableList).removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, itemCount)

                        }
                        .addOnFailureListener {

                        }

                }
                if(zona != null){
                    db.collection("sectores").whereEqualTo("nombre", zona).get().addOnSuccessListener { queryDocumentSnapshots ->
                        for(document in queryDocumentSnapshots.documents){
                            val capacidad = document.getLong("capacidad")?.toInt() ?: 0
                            var capacidadBerria = capacidad+1
                            db.collection("sectores").document(document.id).update("capacidad", capacidadBerria)
                        }
                    }
                }
            }

    }


    override fun getItemCount(): Int = cars.size
}
