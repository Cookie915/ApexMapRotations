package com.example.apexmaprotations.activities

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.apexmaprotations.R
import com.example.apexmaprotations.databinding.ActivityMainBinding
import com.example.apexmaprotations.fragments.ApexFragmentFactory
import com.example.apexmaprotations.viewmodels.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val appViewModel: AppViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(this.layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = ApexFragmentFactory()
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        setUpSplash(splashScreen)
        setContentView(binding.root)
        setupObservables()
    }


    private fun setupObservables() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    appViewModel.backgroundImage.collect() {
                        binding.bgImage.setImageDrawable(it?.let { it1 -> getDrawable(it1) }
                            ?: getDrawable(R.drawable.bg_octane))
                    }
                }
                launch {
                    appViewModel.errStatus.collect() { error ->
                        if (error) {
                            withContext(Dispatchers.Main) {
                                binding.errImage.setImageDrawable(
                                    getDrawable(
                                        appViewModel.errImage.value ?: R.drawable.er_pathy
                                    )
                                )
                                binding.errorMessageTv.text =
                                    appViewModel.errMessage.value ?: "An Unknown Error Occurred"
                                binding.errorMessageTv.visibility = View.VISIBLE
                                binding.errImage.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setUpSplash(splashScreen: SplashScreen) {
        splashScreen.setKeepOnScreenCondition {
            appViewModel.showSplash.value
        }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.TRANSLATION_X,
                0f,
                -splashScreenView.view.width.toFloat()

            ).apply {
                interpolator = DecelerateInterpolator()
                duration = 300L
                doOnEnd {
                    splashScreenView.remove()
                }
            }.start()
        }
    }


}