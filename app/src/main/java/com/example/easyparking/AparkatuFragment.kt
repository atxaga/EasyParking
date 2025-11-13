package com.example.easyparking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.easyparking.databinding.FragmentAparkatuBinding

class AparkatuFragment : Fragment() {

    private var _binding: FragmentAparkatuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAparkatuBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnAparkatu.setOnClickListener {
            val marca = binding.etMarca.text.toString().trim()
            val modelo = binding.etModelo.text.toString().trim()
            val hora = binding.etHora.text.toString().trim()

            if (marca.isEmpty() || modelo.isEmpty()) {
                Toast.makeText(requireContext(), "Introduce marca y modelo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val mensaje = if (hora.isNotEmpty())
                "Aparkatu: $marca $modelo hasta $hora"
            else
                "Aparkatu: $marca $modelo"

            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
        }

        binding.btnAtzera.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
