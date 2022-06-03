package com.example.apexmaprotations.activities


import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.apexmaprotations.R
import com.example.apexmaprotations.databinding.ActivityMainBinding
import com.example.apexmaprotations.fragments.PermissionDialogFragment
import com.example.apexmaprotations.models.Resource
import com.example.apexmaprotations.repo.ALARM_TIME
import com.example.apexmaprotations.repo.NOTIFICATION_TIME
import com.example.apexmaprotations.util.*
import com.example.apexmaprotations.viewmodels.ApexViewModel
import com.example.apexmaprotations.viewmodels.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

const val tag = "MainActivityLogs"

class MainActivity : AppCompatActivity() {
    private val apexViewModel: ApexViewModel by lazy {
        ViewModelProvider(this)[ApexViewModel::class.java]
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@MainActivity
            viewmodel = apexViewModel
        }
    }
    private val alarmManager: AlarmManager by lazy {
        getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    //  offset for animating menu lines
    private var linesOffset by Delegates.notNull<Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("tester", "Main Create")
        binding.menubackground.doOnPreDraw {
            linesOffset = it.height.toFloat() * 1.5f
            binding.menuBackgroundLines.translationY -= linesOffset
        }
        setContentView(binding.root)
        setupObservables()
        lifecycleScope.launch(Dispatchers.IO) {
            val alarmTime = dataStore.data.first()[ALARM_TIME] ?: 0
            val notificationTime = dataStore.data.first()[NOTIFICATION_TIME] ?: 0
            if (alarmTime == 0) {
                binding.alarmButton.progress = 0f
            } else {
                binding.alarmButton.progress = 1f
            }
            if (notificationTime == 0) {
                binding.notifyButton.progress = 0f
            } else {
                binding.notifyButton.progress = 1f
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setUpClickListeners()
        setupAnimationListeners()
    }

    private fun setupAnimationListeners() {
        binding.alarmButton.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                lifecycleScope.launch {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) {
                            val dialog = PermissionDialogFragment()
                            dialog.show(supportFragmentManager, "PermissionDialog")
                        }
                    }
                    if (binding.alarmButton.progress > 0) {
                        scheduleNotification(true, 500)
                    } else {
                        cancelNotification(true)
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
        binding.notifyButton.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                lifecycleScope.launch {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) {
                            val dialog = PermissionDialogFragment()
                            dialog.show(supportFragmentManager, "PermissionDialog")
                        }
                    }
                    if (binding.notifyButton.progress > 0) {
                        scheduleNotification(false, 500)
                    } else {
                        cancelNotification(false)
                    }
                }
            }

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
            when (apexViewModel.showMenu.value) {
                false -> {
                    apexViewModel.showMenu()
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
                    apexViewModel.hideMenu()
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
                    apexViewModel.timeRemaining.map {
                        val hours = it / (1000 * 60 * 60) % 60
                        val minutes = it / (1000 * 60) % 60
                        val seconds = it / (1000) % 60
                        val decimal = it / 100 % 10
                        val times = formatTime(minutes, seconds)
                        binding.time.text = getString(
                            R.string.time_value_format,
                            hours,
                            times.first(),
                            times.last(),
                            decimal
                        )
                    }.collect()
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
                                this.launch {
                                    val alarmTime = dataStore.data.first()[ALARM_TIME]
                                    if (alarmTime != null && alarmTime > 0) {
                                        val valid = verifyAlarms(
                                            alarmTime,
                                            apexViewModel.timeRemaining.first()
                                        )
                                    }
                                }
                                assignMapImage(
                                    it.data.currentMap.map,
                                    binding.currentMapImage,
                                    apexViewModel,
                                    this@MainActivity
                                )
                                assignMapImage(
                                    it.data.nextMap.map,
                                    binding.nextMapImage,
                                    apexViewModel,
                                    this@MainActivity
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// full screen intent
//https://medium.com/android-news/full-screen-intent-notifications-android-85ea2f5b5dc1#:%7E:text=Examples%3A,a%20notification%20with%20high%20priority.