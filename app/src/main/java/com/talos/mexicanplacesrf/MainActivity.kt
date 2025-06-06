package com.talos.mexicanplacesrf

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.talos.mexicanplacesrf.databinding.ActivityMainBinding
import com.talos.mexicanplacesrf.ui.adapters.fragments.PlacesListFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        // si es la primera vez
        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    PlacesListFragment()
                ).commit()
        }
  }
}