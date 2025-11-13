package com.example.easyparking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.easyparking.databinding.FragmentZoneInfoBinding

class ZoneInfoFragment : Fragment() {

    private var _binding: FragmentZoneInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, containerParent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentZoneInfoBinding.inflate(inflater, containerParent, false)

        // Oculto al inicio
        binding.zoneInfoContainer.visibility = View.GONE

        binding.aparkatu.setOnClickListener { aparkatuFunction() }

        return binding.root
    }

    fun updateZoneInfo(name: String, spots: Int) {
        binding.zoneName.text = name
        binding.zoneSpots.text = "Sitios libres: $spots"
        binding.zoneInfoContainer.visibility = View.VISIBLE
    }

    fun hideZoneInfo() {
        binding.zoneInfoContainer.visibility = View.GONE
    }

    private fun aparkatuFunction() {
        val intentAparkatu = Intent(requireContext(), AparkatuActivity::class.java)
        startActivity(intentAparkatu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
