package com.example.easyparking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.easyparking.databinding.ActivityParkedCarsBinding
import com.example.easyparking.databinding.FragmentParkedCarInfoBinding
import com.google.common.net.InetAddresses

class ParkedCarInfoFragment : Fragment() {

    private var _binding: FragmentParkedCarInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, containerParent: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentParkedCarInfoBinding.inflate(inflater, containerParent, false)
        binding.arrowIcon.setOnClickListener {
            var intent = Intent(requireContext(), ParkedCarsActivity::class.java)
            startActivity(intent)
        }
        // Oculto al inicio


        return binding.root

    }



    private fun goToAparkatu() {
        val intent = Intent(requireContext(), AparkatuActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
