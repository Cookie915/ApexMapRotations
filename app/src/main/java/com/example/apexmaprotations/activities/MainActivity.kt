package com.example.apexmaprotations.activities


import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.AlarmManager
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.doOnPreDraw
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.example.apexmaprotations.R
import com.example.apexmaprotations.databinding.ActivityMainBinding
import com.example.apexmaprotations.fragments.PermissionDialogFragment
import com.example.apexmaprotations.models.Resource
import com.example.apexmaprotations.util.*
import com.example.apexmaprotations.viewmodels.AppViewModel
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel
import com.example.apexmaprotations.viewmodels.dataStore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

const val tag = "MainActivityLogs"

class MainActivity : AppCompatActivity() {
    private val battleRoyalViewModel: BattleRoyalViewModel by lazy {
        ViewModelProvider(this)[BattleRoyalViewModel::class.java]
    }
    private val appViewModel: AppViewModel by lazy {
        ViewModelProvider(this)[AppViewModel::class.java]
    }
    private val alarmButton: LottieAnimationView by lazy {
        binding.alarmButton
    }
    private val notifyButton: LottieAnimationView by lazy {
        binding.notifyButton
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@MainActivity
            viewmodel = battleRoyalViewModel
        }
    }
    private val alarmManager: AlarmManager by lazy {
        getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    //  offset for animating menu lines
    private var linesOffset by Delegates.notNull<Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding.menubackground.doOnPreDraw {
            linesOffset = it.height.toFloat() * 1.5f
            binding.menuBackgroundLines.translationY -= linesOffset
        }
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
        // fix
//        verifyAlarms()
        setContentView(binding.root)
        setupObservables()
    }

    override fun onStart() {
        super.onStart()
        setUpClickListeners()
        setupAnimationListeners()
        listenToAlarms()
        lottieListeners()
    }


    private fun listenToAlarms() {
        lifecycleScope.launch {
            dataStore.data.map {
                val alarmTime = it[ALARM_TIME] ?: 0L
                val notifyTime = it[NOTIFICATION_TIME] ?: 0L
                if (alarmTime == 0L) {
                    binding.alarmButton.progress = 0f
                } else {
                    binding.notifyButton.isEnabled = false
                    binding.alarmButton.progress = 1f
                }
                if (notifyTime == 0L) {
                    binding.notifyButton.progress = 0f
                } else {
                    binding.alarmButton.isEnabled = false
                    binding.notifyButton.progress = 1f
                }
                if (alarmTime == 0L && notifyTime == 0L) {
                    binding.notifyButton.isEnabled = true
                    binding.alarmButton.isEnabled = true
                }
            }.collect()
        }
    }

    private fun setupAnimationListeners() {
        binding.alarmButton.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                lifecycleScope.launch {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) {
                            val dialog = PermissionDialogFragment()
                            dialog.show(supportFragmentManager, "PermissionDialog")
                        }
                    }
                    if (binding.alarmButton.progress > 0) {
                        scheduleNotification(true, battleRoyalViewModel.timeRemainingLong.value)
                    } else {
                        binding.notifyButton.isEnabled = true
                        cancelNotification(true)
                    }
                }
            }

            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        })
        binding.notifyButton.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                lifecycleScope.launch {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) {
                            val dialog = PermissionDialogFragment()
                            dialog.show(supportFragmentManager, "PermissionDialog")
                        }
                    }
                    if (binding.notifyButton.progress > 0) {
                        scheduleNotification(false, battleRoyalViewModel.timeRemainingLong.value)
                    } else {
                        binding.alarmButton.isEnabled = true
                        cancelNotification(false)
                    }
                }
            }

            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        })
    }

    private fun setUpClickListeners() {
        val notifyButton = binding.notifyButton
        val alarmButton = binding.alarmButton
        val menubackground = binding.menubackground
        notifyButton.setOnClickListener {
            if (notifyButton.progress == 0f) {
                notifyButton.speed = 1f
                notifyButton.playAnimation()
            } else {
                notifyButton.speed = -1f
                notifyButton.playAnimation()
            }
        }
        alarmButton.setOnClickListener {
            if (alarmButton.progress == 0f) {
                alarmButton.speed = 1f
                alarmButton.playAnimation()
            } else {
                alarmButton.speed = -1f
                alarmButton.playAnimation()
            }
        }
        menubackground.setOnClickListener {
            when (appViewModel.showMenu.value) {
                false -> {
                    appViewModel.showMenu()
                    notifyButton.isClickable = true
                    alarmButton.isClickable = true
                    binding.time.visibility = View.INVISIBLE
                    binding.timerAnimation.visibility = View.INVISIBLE
                    binding.topText.visibility = View.INVISIBLE
                    binding.bottomText.visibility = View.INVISIBLE
                    ObjectAnimator.ofFloat(binding.menuBackgroundLines, "translationY", 0f).apply {
                        duration = 800
                        start()
                    }
                    ObjectAnimator.ofFloat(menubackground, "alpha", 0.8f).apply {
                        interpolator = LinearInterpolator()
                        duration = 800
                        start()
                    }
                    ObjectAnimator.ofFloat(notifyButton, "alpha", 1f).apply {
                        interpolator = LinearInterpolator()
                        duration = 600
                        start()
                    }
                    ObjectAnimator.ofFloat(alarmButton, "alpha", 1f).apply {
                        interpolator = LinearInterpolator()
                        duration = 600
                        start()
                    }
                }
                true -> {
                    notifyButton.isClickable = false
                    alarmButton.isClickable = false
                    appViewModel.hideMenu()
                    binding.time.visibility = View.VISIBLE
                    binding.timerAnimation.visibility = View.VISIBLE
                    binding.topText.visibility = View.VISIBLE
                    binding.bottomText.visibility = View.VISIBLE
                    ObjectAnimator.ofFloat(
                        binding.menuBackgroundLines,
                        "translationY",
                        -linesOffset
                    ).apply {
                        duration = 800
                        start()
                    }
                    ObjectAnimator.ofFloat(menubackground, "alpha", 0f).apply {
                        interpolator = LinearInterpolator()
                        duration = 800
                        start()
                    }
                    ObjectAnimator.ofFloat(notifyButton, "alpha", 0f).apply {
                        interpolator = LinearInterpolator()
                        duration = 600
                        start()
                    }
                    ObjectAnimator.ofFloat(alarmButton, "alpha", 0f).apply {
                        interpolator = LinearInterpolator()
                        duration = 600
                        start()
                    }
                }
            }
        }
    }

    private fun setupObservables() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    battleRoyalViewModel.timeRemaining.map {
                        binding.time.text = getString(
                            R.string.time_value_format,
                            it[0].toString().toLong(),
                            it[1].toString(),
                            it[2].toString(),
                            it[3].toString().toLong()
                        )
                    }.collect()
                }
                launch {
                    battleRoyalViewModel.mapDataBundle.collect { mapData ->
                        when (mapData) {
                            is Resource.Loading -> {
                                //todo show loading
                            }
                            is Resource.Failure -> {
                                //  todo error screen
                            }
                            is Resource.Success -> {
                                appViewModel.hideSplash()
                                Log.i("tester", mapData.data?.toString() ?: "null")
                                val currentMap = mapData.data!!.battleRoyale.current
                                dataStore.edit {
                                    it[NEXT_MAP] = currentMap.map
                                }
                                battleRoyalViewModel.initializeTimer(mapData.data.battleRoyale)
                                assignMapImage(
                                    currentMap.map,
                                    binding.currentMapImage,
                                    battleRoyalViewModel,
                                    this@MainActivity
                                )
                                assignMapImage(
                                    mapData.data.battleRoyale.next.map,
                                    binding.nextMapImage,
                                    battleRoyalViewModel,
                                    this@MainActivity
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun lottieListeners() {
        notifyButton.addLottieOnCompositionLoadedListener {
            notifyButton.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER) {
                if (alarmButton.progress > 0f) PorterDuffColorFilter(
                    Color.DKGRAY,
                    PorterDuff.Mode.DARKEN
                ) else SimpleColorFilter(Color.TRANSPARENT)
            }
        }
        alarmButton.addLottieOnCompositionLoadedListener {
            alarmButton.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER) {
                if (notifyButton.progress > 0f) PorterDuffColorFilter(
                    Color.DKGRAY,
                    PorterDuff.Mode.DARKEN
                ) else SimpleColorFilter(Color.TRANSPARENT)
            }
        }
    }

}

//  TODO
//  Make splashscreen compatible on newer android versions

