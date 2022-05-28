package com.example.apexmaprotations


import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.apexmaprotations.databinding.ActivityMainBinding
import com.example.apexmaprotations.models.Resource
import com.example.apexmaprotations.repo.assignMapImage
import com.example.apexmaprotations.repo.formatTime
import com.example.apexmaprotations.viewmodels.ApexViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val tag = "MainActivityLogs"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var apexViewModel: ApexViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apexViewModel = ViewModelProvider(this)[ApexViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@MainActivity
            viewmodel = apexViewModel
        }
        val currentMap = binding.currentMapImage
        val nextMap = binding.nextMapImage
        val time = binding.time
        val animation = binding.animation
        val topText = binding.topText
        val bottomText = binding.bottomText
        val menuLines = binding.menuBackgroundLines
        val menuBackground = binding.menubackground
        menuBackground.doOnPreDraw {
            menuLines.translationY = -it.height.toFloat() * 2f
        }
        setContentView(binding.root)
        binding.menubackground.setOnClickListener { v ->
            when (apexViewModel.showMenu.value) {
                false -> {
                    apexViewModel.showMenu()
                    time.visibility = View.INVISIBLE
                    animation.visibility = View.INVISIBLE
                    topText.visibility = View.INVISIBLE
                    bottomText.visibility = View.INVISIBLE
                    v?.setBackgroundColor(ContextCompat.getColor(this, R.color.menu_background))
                    ObjectAnimator.ofFloat(menuLines, "translationY", 0f).apply {
                        interpolator = FastOutSlowInInterpolator()
                        duration = 1500
                        start()
                    }
                }
                true -> {
                    apexViewModel.hideMenu()
                    time.visibility = View.VISIBLE
                    animation.visibility = View.VISIBLE
                    topText.visibility = View.VISIBLE
                    bottomText.visibility = View.VISIBLE
                    v?.setBackgroundColor(Color.TRANSPARENT)
                    ObjectAnimator.ofFloat(
                        menuLines,
                        "translationY",
                        -menuBackground.height.toFloat() * 2f
                    ).apply {
                        interpolator = FastOutSlowInInterpolator()
                        duration = 1500
                        start()
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    apexViewModel.timeRemaining.map {
                        val hours = it / (1000 * 60 * 60) % 60
                        val minutes = it / (1000 * 60) % 60
                        val seconds = it / (1000) % 60
                        val decimal = it / 100 % 10
                        val times = formatTime(minutes, seconds)
                        time.text = getString(
                            R.string.time_value_format,
                            hours,
                            times.first(),
                            times.last(),
                            decimal
                        )
                    } .collect()
                }
                launch {
                    apexViewModel.mapData.collect {
                        when (it) {
                            is Resource.Loading -> {
                                //todo show loading
                            }
                            is Resource.Failure -> {
                                //  todo error screen
                            }
                            is Resource.Success -> {
                                Log.i(tag, it.data!!.currentMap.map)
                                assignMapImage(
                                    it.data.currentMap.map,
                                    currentMap,
                                    apexViewModel,
                                    this@MainActivity
                                )
                                assignMapImage(it.data.nextMap.map, nextMap, apexViewModel, this@MainActivity)
                            }
                        }
                    }
                }
            }
        }
    }
}