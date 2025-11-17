package com.example.easyparking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.easyparking.databinding.FragmentFooterBinding

class FooterFragment : Fragment() {

    private var _binding: FragmentFooterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFooterBinding.inflate(inflater, container, false)

        val currentTab = when (requireActivity()) {
            is MainActivity         -> "bilatu"
            is ParkedCarsActivity   -> "aparkatuak"
            is CarsActivity         -> "autoak"
            is ActivityCuenta       -> "kontua"
            else                    -> "bilatu"
        }

        setActiveTab(currentTab)

        binding.bilatuLayout.setOnClickListener { navigateTo(MainActivity::class.java) }
        binding.aparkatuLayout.setOnClickListener { navigateTo(ParkedCarsActivity::class.java) }
        binding.autoakLayout.setOnClickListener { navigateTo(CarsActivity::class.java) }
        binding.kontuaLayout.setOnClickListener { navigateTo(ActivityCuenta::class.java) }

        return binding.root
    }

    private fun navigateTo(activityClass: Class<*>) {
        if (activityClass == requireActivity()::class.java) return

        val intent = Intent(requireContext(), activityClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
        requireActivity().overridePendingTransition(0, 0)
    }

    private fun setActiveTab(tab: String) = with(binding) {
        bilatuIndicator.visibility     = if (tab == "bilatu")      View.VISIBLE else View.GONE
        aparkatuIndicator.visibility   = if (tab == "aparkatuak")  View.VISIBLE else View.GONE
        autoakIndicator.visibility     = if (tab == "autoak")      View.VISIBLE else View.GONE
        kontuaIndicator.visibility     = if (tab == "kontua")      View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null


    }
}