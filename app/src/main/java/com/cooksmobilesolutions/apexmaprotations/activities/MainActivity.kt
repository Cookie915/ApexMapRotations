package com.cooksmobilesolutions.apexmaprotations.activities

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.cooksmobilesolutions.apexmaprotations.R
import com.cooksmobilesolutions.apexmaprotations.databinding.ActivityMainBinding
import com.cooksmobilesolutions.apexmaprotations.di.DispatcherProvider
import com.cooksmobilesolutions.apexmaprotations.fragments.ArenasFragment
import com.cooksmobilesolutions.apexmaprotations.fragments.BattleRoyalFragment
import com.cooksmobilesolutions.apexmaprotations.util.SHOW_INTO_SCREEN_KEY
import com.cooksmobilesolutions.apexmaprotations.util.SwipeGestureListener
import com.cooksmobilesolutions.apexmaprotations.util.SwipeListener
import com.cooksmobilesolutions.apexmaprotations.viewmodels.ApexViewModel
import com.cooksmobilesolutions.apexmaprotations.viewmodels.AppViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NUM_PAGES = 2

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SwipeListener {
    private lateinit var viewPager: ViewPager2

    @Inject
    lateinit var dispatchers: DispatcherProvider

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val appViewModel: AppViewModel by viewModels()
    val apexViewModel: ApexViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(this.layoutInflater)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("DisplayMetrics", (resources.displayMetrics.ydpi).toString())
        appViewModel.verifyAlerts(this)
        appViewModel.setBackgroundImage(this)
        val splashScreen = installSplashScreen()
        setUpSplash(splashScreen)
        setContentView(binding.root)
        setupObservables()
        binding.refreshDataButton.setOnClickListener {
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show()
            apexViewModel.refreshMapData()
        }

        viewPager = findViewById(R.id.viewPager)
        val pagerAdapter = ScreenSlidePageAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false
        binding.root.setOnTouchListener(SwipeGestureListener(this))
    }

    override fun onResume() {
        super.onResume()
        //  Show intro screen if first launch
        if (sharedPreferences.getBoolean(SHOW_INTO_SCREEN_KEY, true)) {
            val intent = Intent(this@MainActivity, IntroScreenActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    private inner class ScreenSlidePageAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    BattleRoyalFragment()
                }
                1 -> {
                    ArenasFragment()
                }
                else -> BattleRoyalFragment()
            }
        }
    }

    private fun setupObservables() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    appViewModel.backgroundImage.collect {
                        if (it != null) {
                            Glide.with(this@MainActivity)
                                .load(it)
                                .error(R.drawable.bg_octane)
                                .into(binding.bgImage)
                        }
                    }
                }
                launch {
                    appViewModel.errStatus.collect { error ->
                        if (error) {
                            Glide.with(this@MainActivity)
                                .load(appViewModel.errImage.value)
                                .error(R.drawable.er_pathy)
                                .into(binding.errImage)
                            binding.errorMessageTv.text =
                                appViewModel.errMessage.value ?: "An Unknown Error Occurred"
                            binding.errorMessageTv.visibility = View.VISIBLE
                            binding.errImage.visibility = View.VISIBLE
                            binding.refreshDataButton.visibility = View.VISIBLE
                        } else {
                            binding.errImage.visibility = View.INVISIBLE
                            binding.errorMessageTv.text = ""
                            binding.errorMessageTv.visibility = View.INVISIBLE
                            binding.refreshDataButton.visibility = View.GONE
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

    override fun onSwipeDown() {
        Snackbar.make(binding.root, "refreshing", 2500).show()
        apexViewModel.refreshMapData()
    }

    override fun onSwipeUp() {}

    override fun onSwipeLeft() {
        Log.i("tester14", "Swipe Left")
        if (viewPager.currentItem == 0) {
            viewPager.currentItem = 1
        }
    }

    override fun onSwipeRight() {
        Log.i("tester14", "Swipe Right")
        if (viewPager.currentItem == 1) {
            viewPager.currentItem = 0
        }
    }


}