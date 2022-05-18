package com.example.apexmaprotations

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.apexmaprotations.databinding.ActivityMainBinding
import com.example.apexmaprotations.viewmodels.ApexViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var apexViewModel: ApexViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apexViewModel = ViewModelProvider(this)[ApexViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewmodel = apexViewModel
        val view = binding.root
        val currentMapImage = binding.currentMapImage
        val nextMapImage = binding.nextMapImage

        Glide.with(this)
            .load("https://apexlegendsstatus.com//assets//maps//Worlds_Edge.png")
            .centerCrop()
            .into(currentMapImage)

        Glide.with(this)
            .load("https://apexlegendsstatus.com//assets//maps//Kings_Canyon.png")
            .centerCrop()
            .into(nextMapImage)

        setContentView(view)
    }
}