package com.example.easyparking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class AparkatuFragment : Fragment() {

    private lateinit var etMarca: EditText
    private lateinit var etModelo: EditText
    private lateinit var etHora: EditText
    private lateinit var btnAparkatu: Button
    private lateinit var btnAtzera: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_aparkatu, container, false)

        etMarca = view.findViewById(R.id.etMarca)
        etModelo = view.findViewById(R.id.etModelo)
        etHora = view.findViewById(R.id.etHora)
        btnAparkatu = view.findViewById(R.id.btnAparkatu)
        btnAtzera = view.findViewById(R.id.btnAtzera)

        btnAparkatu.setOnClickListener {
            val marca = etMarca.text.toString().trim()
            val modelo = etModelo.text.toString().trim()
            val hora = etHora.text.toString().trim()

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

        btnAtzera.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return view
    }
}
