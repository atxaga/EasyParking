package com.example.easyparking

import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment

class FooterFragment : Fragment() {

    private lateinit var bilatuIndicator: View
    private lateinit var aparkatuakIndicator: View
    private lateinit var autoakIndicator: View
    private lateinit var kontuaIndicator: View
    private lateinit var autoakButton: View
    private lateinit var bilatuButton: View
    private lateinit var aparkatuakButton: View

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
        autoakButton = view.findViewById(R.id.autoakLayout)
        bilatuButton = view.findViewById(R.id.bilatuLayout)
        aparkatuakButton = view.findViewById(R.id.aparkatuLayout)

        setActiveTab("bilatu")

        view.findViewById<View>(R.id.bilatuLayout).setOnClickListener { setActiveTab("bilatu") }
        view.findViewById<View>(R.id.aparkatuLayout).setOnClickListener { setActiveTab("aparkatuak") }
        view.findViewById<View>(R.id.autoakLayout).setOnClickListener { setActiveTab("autoak") }
        view.findViewById<View>(R.id.kontuaLayout).setOnClickListener { setActiveTab("kontua") }

        autoakButton.setOnClickListener { autoakview() }
        bilatuButton.setOnClickListener { bilatuView() }
        aparkatuakButton.setOnClickListener { aparkatuakView() }




        return view
    }

    private fun setActiveTab(tab: String) {
        bilatuIndicator.visibility = if (tab == "bilatu") View.VISIBLE else View.GONE
        aparkatuakIndicator.visibility = if (tab == "aparkatuak") View.VISIBLE else View.GONE
        autoakIndicator.visibility = if (tab == "autoak") View.VISIBLE else View.GONE
        kontuaIndicator.visibility = if (tab == "kontua") View.VISIBLE else View.GONE
    }
    fun autoakview(){
        val intent = Intent(requireContext(), CarsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivity(intent)
    }
    fun bilatuView(){
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivity(intent)

    }
    fun aparkatuakView(){
        val intent = Intent(requireContext(), ParkedCarsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivity(intent)

    }
}
