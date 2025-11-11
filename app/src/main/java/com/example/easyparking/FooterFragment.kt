package com.example.easyparking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FooterFragment : Fragment() {

    private lateinit var bilatuIndicator: View
    private lateinit var aparkatuakIndicator: View
    private lateinit var autoakIndicator: View
    private lateinit var kontuaIndicator: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_footer, container, false)

        bilatuIndicator = view.findViewById(R.id.bilatuIndicator)
        aparkatuakIndicator = view.findViewById(R.id.aparkatuIndicator)
        autoakIndicator = view.findViewById(R.id.autoakIndicator)
        kontuaIndicator = view.findViewById(R.id.kontuaIndicator)

        setActiveTab("bilatu")

        view.findViewById<View>(R.id.bilatuLayout).setOnClickListener { setActiveTab("bilatu") }
        view.findViewById<View>(R.id.aparkatuLayout).setOnClickListener { setActiveTab("aparkatuak") }
        view.findViewById<View>(R.id.autoakLayout).setOnClickListener { setActiveTab("autoak") }
        view.findViewById<View>(R.id.kontuaLayout).setOnClickListener { setActiveTab("kontua") }

        return view
    }

    private fun setActiveTab(tab: String) {
        bilatuIndicator.visibility = if (tab == "bilatu") View.VISIBLE else View.GONE
        aparkatuakIndicator.visibility = if (tab == "aparkatuak") View.VISIBLE else View.GONE
        autoakIndicator.visibility = if (tab == "autoak") View.VISIBLE else View.GONE
        kontuaIndicator.visibility = if (tab == "kontua") View.VISIBLE else View.GONE
    }
}
