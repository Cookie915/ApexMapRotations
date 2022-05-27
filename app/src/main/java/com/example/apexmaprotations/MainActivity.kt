package com.example.apexmaprotations


import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.apexmaprotations.databinding.ActivityMainBinding
import com.example.apexmaprotations.repo.formatTime
import com.example.apexmaprotations.viewmodels.ApexViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var apexViewModel: ApexViewModel
    val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.argb(248, 255, 255, 255)
        strokeWidth = 20f
        style = Paint.Style.STROKE
    }
    val paintBlur = Paint().apply {
        set(paint)
        color = Color.argb(235, 74, 138, 255)
        strokeWidth = 30f
        maskFilter = BlurMaskFilter(15F, BlurMaskFilter.Blur.NORMAL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apexViewModel = ViewModelProvider(this)[ApexViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@MainActivity
            viewmodel = apexViewModel
        }
        val view = binding.root
        val currentMap = binding.currentMapImage
        val nextMap = binding.nextMapImage
        val time = binding.time
        time.setShadowLayer(300F, 0F, 0F, Color.WHITE)
        lifecycleScope.launch {
            apexViewModel.timeRemaining.map {
                val hours = it / (1000*60*60) % 60
                val minutes = it / (1000 * 60) % 60
                val seconds = it / (1000) % 60
                val decimal = it / 100 % 10
                val times = formatTime( minutes, seconds)
                time.text = getString(R.string.time_value_format, hours, times.first(), times.last(), decimal)
            } .collect()
        }
        Glide.with(this)
            .load("https://apexlegendsstatus.com/assets/maps/Olympus.png")
            .centerCrop()
            .into(currentMap)

        Glide.with(this)
            .load("https://apexlegendsstatus.com/assets/maps/Worlds_Edge.png")
            .centerCrop()
            .into(nextMap)
        setContentView(view)

    }
    override fun onStop() {
        super.onStop()
    }
}