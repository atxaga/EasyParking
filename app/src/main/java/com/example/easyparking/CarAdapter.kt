import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easyparking.CarsActivity
import com.example.easyparking.R

data class Car(
    var marca: String = "",
    var modelo: String = "",
    var matricula: String = "",
    var user_id: String = "",
    var zona: String? = null
)

class CarAdapter(private val cars: List<Car>) :
    RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandText: TextView = itemView.findViewById(R.id.brandText)
        val modelText: TextView = itemView.findViewById(R.id.modelText)
        val plateText: TextView = itemView.findViewById(R.id.plateText)
        val basura: ImageView = itemView.findViewById(R.id.basura)
        val carIcon: ImageView = itemView.findViewById(R.id.carIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]
        holder.brandText.text = car.marca
        holder.modelText.text = car.modelo
        holder.plateText.text = car.matricula

        holder.basura.setOnClickListener {
            com.google.android.material.dialog.MaterialAlertDialogBuilder(holder.itemView.context)
                .setTitle("Ziur zaude?")
                .setMessage("Benetan ezabatu nahi duzu auto hau: ${car.matricula}?")
                .setIcon(R.drawable.ic_basura) // icono de basura bonito
                .setPositiveButton("Bai") { dialog, _ ->
                    (holder.itemView.context as CarsActivity).borratuKotxe(car.user_id!!)
                    dialog.dismiss()
                }
                .setNegativeButton("Ez") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


    }

    override fun getItemCount(): Int = cars.size
}
