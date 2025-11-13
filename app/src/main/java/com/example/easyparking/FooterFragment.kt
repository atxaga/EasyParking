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

    private var activeTab: String = "bilatu" // default inicial

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFooterBinding.inflate(inflater, container, false)

        // Leer tab activo de SharedPreferences
        val prefs = requireContext().getSharedPreferences("footer_prefs", Context.MODE_PRIVATE)
        activeTab = prefs.getString("active_tab", "bilatu") ?: "bilatu"

        // Marcar la barra cyan según el tab activo
        setActiveTab(activeTab)

        // Listeners de botones
        binding.bilatuLayout.setOnClickListener { onTabClicked("bilatu") }
        binding.aparkatuLayout.setOnClickListener { onTabClicked("aparkatuak") }
        binding.autoakLayout.setOnClickListener { onTabClicked("autoak") }
        binding.kontuaLayout.setOnClickListener { onTabClicked("kontua") }

        return binding.root
    }

    private fun onTabClicked(tab: String) {
        if (activeTab == tab) return // ya está activo

        activeTab = tab
        setActiveTab(tab)

        // Guardar tab en SharedPreferences
        val prefs = requireContext().getSharedPreferences("footer_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("active_tab", tab).apply()

        // Navegar a la Activity correspondiente
        val intent = when (tab) {
            "bilatu" -> Intent(requireContext(), MainActivity::class.java)
            "aparkatuak" -> Intent(requireContext(), ParkedCarsActivity::class.java)
            "autoak" -> Intent(requireContext(), CarsActivity::class.java)
            "kontua" -> Intent(requireContext(), ActivityCuenta::class.java)
            else -> return
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }

    private fun setActiveTab(tab: String) = with(binding) {
        bilatuIndicator.visibility = if (tab == "bilatu") View.VISIBLE else View.GONE
        aparkatuIndicator.visibility = if (tab == "aparkatuak") View.VISIBLE else View.GONE
        autoakIndicator.visibility = if (tab == "autoak") View.VISIBLE else View.GONE
        kontuaIndicator.visibility = if (tab == "kontua") View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
