import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easyparking.R

class ParkedCarAdapter(private val cars: List<ParkedCar>) :
    RecyclerView.Adapter<ParkedCarAdapter.ParkedCarViewHolder>() {

    inner class ParkedCarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandText: TextView = itemView.findViewById(R.id.brandText)
        val modelText: TextView = itemView.findViewById(R.id.modelText)
        val plateText: TextView = itemView.findViewById(R.id.plateText)
        val zoneText: TextView = itemView.findViewById(R.id.zoneText)
        val carIcon: ImageView = itemView.findViewById(R.id.carIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkedCarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parked_car, parent, false)
        return ParkedCarViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParkedCarViewHolder, position: Int) {
        val car = cars[position]
        holder.brandText.text = car.brand
        holder.modelText.text = car.model
        holder.plateText.text = car.plate
        holder.zoneText.text = "Zona: ${car.zone}"
        holder.carIcon.setImageResource(R.drawable.coche)
    }

    override fun getItemCount(): Int = cars.size
}
