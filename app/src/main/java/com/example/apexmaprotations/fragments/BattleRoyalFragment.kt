package com.example.apexmaprotations.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.AlarmManager
import android.content.Context.ALARM_SERVICE
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.example.apexmaprotations.R
import com.example.apexmaprotations.databinding.FragmentBattleroyaleBinding
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.util.*
import com.example.apexmaprotations.viewmodels.AppViewModel
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel
import com.example.apexmaprotations.viewmodels.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val TAG = "BattleRoyalFragment"

@AndroidEntryPoint
class BattleRoyalFragment : Fragment(R.layout.fragment_battleroyale), SwipeListener {
    private val navController: NavController by lazy {
        findNavController()
    }
    private val transitionInflater: TransitionInflater by lazy {
        TransitionInflater.from(requireContext())
    }
    private val battleRoyalViewModel: BattleRoyalViewModel by viewModels()
    private val appViewModel: AppViewModel by activityViewModels()
    private val binding: FragmentBattleroyaleBinding by lazy {
        FragmentBattleroyaleBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@BattleRoyalFragment
            viewmodel = battleRoyalViewModel
        }
    }
    private val alarmButton: LottieAnimationView by lazy { binding.alarmButton }
    private val notifyButton: LottieAnimationView by lazy { binding.notifyButton }
    private val alarmManager: AlarmManager by lazy {
        requireActivity().getSystemService(
            ALARM_SERVICE
        ) as AlarmManager
    }

    //  offset for animating menu lines
    private val linesOffset: Float by lazy {
        requireContext().resources.displayMetrics.heightPixels.toFloat() * 1.5f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.menuBackgroundLines.doOnPreDraw {
            it.translationY -= linesOffset
        }
        //  Manually set X to avoid jitter caused by centering text as decimal place in timer changes
        binding.time.doOnPreDraw {
            val viewWidth = it.width / 2
            val width = resources.displayMetrics.widthPixels / 2 - viewWidth
            binding.time.translationX += width
        }
        exitTransition = transitionInflater.inflateTransition(R.transition.fade)
        // fix
        // verifyAlarms()
        setupObservables()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListeners()
        setupAnimationListeners()
        listenToAlarms()
        lottieListeners()
    }

    private fun setupObservables() {
        Log.i(TAG, "setupobs")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    battleRoyalViewModel.currentMapImage.collect() {
                        if (it != null) {
                            binding.currentMapImage.setImageDrawable(
                                requireActivity().getDrawable(
                                    it
                                )
                            )
                        }
                    }
                }
                launch {
                    battleRoyalViewModel.nextMapImage.collect() {
                        if (it != null) {
                            binding.nextMapImage.setImageDrawable(requireActivity().getDrawable(it))
                        }
                    }
                }
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
                            is NetworkResult.Loading -> {
                                Log.i(TAG, "mapdata Loading from setupobvs()")
                                //todo show loading
                            }
                            is NetworkResult.Error -> {
                                Log.i(TAG, "mapdata Error from setupobvs()")
                                Toast.makeText(
                                    requireContext(),
                                    "MapData Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is NetworkResult.Success -> {
                                Log.i(TAG, "mapdata Success from setupobvs()")
                                appViewModel.hideSplash()
                                requireActivity().dataStore.edit {
                                    it[NEXT_MAP] = mapData.data!!.battleRoyale.next.map
                                }
                            }
                        }
                    }
                }
                launch {
                    appViewModel.showMenu.collect { showMenu ->
                        when (showMenu) {
                            true -> {
                                notifyButton.isClickable = true
                                alarmButton.isClickable = true
                                binding.time.visibility = View.INVISIBLE
                                binding.timerAnimation.visibility = View.INVISIBLE
                                binding.topText.visibility = View.INVISIBLE
                                binding.bottomText.visibility = View.INVISIBLE
                                ObjectAnimator.ofFloat(
                                    binding.menuBackgroundLines,
                                    "translationY",
                                    0f
                                )
                                    .apply {
                                        duration = 800
                                        start()
                                    }
                                ObjectAnimator.ofFloat(binding.menubackground, "alpha", 0.8f)
                                    .apply {
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
                            false -> {
                                notifyButton.isClickable = false
                                alarmButton.isClickable = false
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
                                ObjectAnimator.ofFloat(binding.menubackground, "alpha", 0f)
                                    .apply {
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
            }
        }
    }

    private fun setupAnimationListeners() {
        binding.alarmButton.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                lifecycleScope.launch {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) {
                            val dialog = PermissionDialogFragment()
                            dialog.show(
                                requireActivity().supportFragmentManager,
                                "PermissionDialog"
                            )
                        }
                    }
                    if (binding.alarmButton.progress > 0) {
                        requireContext().scheduleNotification(
                            true,
                            battleRoyalViewModel.timeRemainingLong.value
                        )
                    } else {
                        binding.notifyButton.isEnabled = true
                        requireContext().cancelNotification(true)
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
                            dialog.show(
                                requireActivity().supportFragmentManager,
                                "PermissionDialog"
                            )
                        }
                    }
                    if (binding.notifyButton.progress > 0) {
                        requireContext().scheduleNotification(
                            false,
                            battleRoyalViewModel.timeRemainingLong.value
                        )
                    } else {
                        binding.alarmButton.isEnabled = true
                        requireContext().cancelNotification(false)
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
        menubackground.setOnTouchListener(SwipeGestureListener(this))
        binding.arrowRight.setOnClickListener {
            navController.navigate(BattleRoyalFragmentDirections.actionBattleRoyalFragmentToArenasFragment())
        }
        menubackground.setOnLongClickListener {
            when (appViewModel.showMenu.value) {
                true -> {
                    appViewModel.hideMenu()
                }
                false -> {
                    appViewModel.showMenu()
                }
            }
            return@setOnLongClickListener true
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

    private fun listenToAlarms() {
        lifecycleScope.launch {
            requireActivity().dataStore.data.map {
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

    override fun onSwipeLeft() {
        navController.navigate(BattleRoyalFragmentDirections.actionBattleRoyalFragmentToArenasFragment())
    }

    override fun onSwipeRight() {}

}