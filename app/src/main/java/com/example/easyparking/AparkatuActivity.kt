package com.example.easyparking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.easyparking.databinding.ActivityAparkatuBinding

class AparkatuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAparkatuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAparkatuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAtzera.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
