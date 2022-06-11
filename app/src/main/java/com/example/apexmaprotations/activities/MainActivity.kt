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
import com.example.apexmaprotations.R
import com.example.apexmaprotations.viewmodels.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val appViewModel: AppViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        setUpSplash(splashScreen)
        setContentView(R.layout.activity_main)
    }


    fun setUpSplash(splashScreen: SplashScreen) {
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