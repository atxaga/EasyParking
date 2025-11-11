package com.example.easyparking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cargamos los fragmentos solo la primera vez
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)

                // Fragmento del mapa
                replace(R.id.mapFragmentContainer, MapFragment())

            }
        }
    }
}
