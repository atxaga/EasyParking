package com.example.easyparking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ZoneInfoFragment : Fragment() {

    private lateinit var zoneNameText: TextView
    private lateinit var zoneSpotsText: TextView
    private lateinit var container: View
    private lateinit var aparkatu: Button

    override fun onCreateView(
        inflater: LayoutInflater, containerParent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_zone_info, containerParent, false)
        container = view.findViewById(R.id.zoneInfoContainer)
        zoneNameText = view.findViewById(R.id.zoneName)
        zoneSpotsText = view.findViewById(R.id.zoneSpots)
        aparkatu = view.findViewById(R.id.aparkatu)

        // Oculto al inicio
        container.visibility = View.GONE

        aparkatu.setOnClickListener { aparkatuFunction() }

        return view
    }

    fun updateZoneInfo(name: String, spots: Int) {
        zoneNameText.text = name
        zoneSpotsText.text = "Sitios libres: $spots"
        container.visibility = View.VISIBLE
    }

    fun hideZoneInfo() {
        container.visibility = View.GONE
    }
    private fun aparkatuFunction() {
        val intentAparkatu = Intent(requireContext(), AparkatuActivity::class.java)
        startActivity(intentAparkatu)
    }
}
