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
import androidx.core.view.OneShotPreDrawListener
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
import com.example.apexmaprotations.viewmodels.ArenasViewModel
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel
import com.example.apexmaprotations.viewmodels.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val TAG = "BattleRoyalFragment"

@AndroidEntryPoint
class BattleRoyalFragment : Fragment(R.layout.fragment_battleroyale), SwipeListener {
    private val navController: NavController by lazy { findNavController() }
    private val transitionInflater: TransitionInflater by lazy {
        TransitionInflater.from(
            requireContext()
        )
    }
    private val battleRoyalViewModel: BattleRoyalViewModel by viewModels()
    private val arenaViewModel: ArenasViewModel by activityViewModels()
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
    private val backgroundViews by lazy {
        listOf(
            binding.alarmLabel,
            binding.notifyLabel,
            binding.alarmButton,
            binding.notifyButton
        )
    }
    private val foregroundViews by lazy {
        listOf(
            binding.currentMapName,
            binding.nextMapName,
            binding.timerAnimation,
            binding.time,
            binding.topText,
            binding.bottomText,
            binding.arrowRight
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = transitionInflater.inflateTransition(R.transition.fade)
        // fix
        // verifyAlerts()
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
        OneShotPreDrawListener.add(binding.menuBackgroundLines) {
            binding.menuBackgroundLines.translationY -= linesOffset
        }
        setUpClickListeners()
        setupAnimationListeners()
        listenToAlarms()
        lottieListeners()
    }

    private fun setupObservables() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    battleRoyalViewModel.currentMapImage.collectLatest {
                        if (it != null) {
                            binding.currentMapImage.setImageDrawable(
                                requireActivity().getDrawable(it)
                            )
                        }
                    }
                }
                launch {
                    battleRoyalViewModel.nextMapImage.collectLatest {
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
                    battleRoyalViewModel.mapDataBundle.collectLatest { mapData ->
                        when (mapData) {
                            is NetworkResult.Loading -> {
                                Log.i(TAG, "mapdata Loading from setupobvs()")
                                Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT)
                                    .show()
                                //todo show loading
                            }
                            is NetworkResult.Error -> {
                                Log.i(TAG, "mapdata Error from setupobvs()")
                                Toast.makeText(
                                    requireContext(),
                                    "MapData Failed ${mapData.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is NetworkResult.Success -> {
                                Log.i(TAG, "mapdata Success from setupobvs()")
                                binding.currentMapName.text =
                                    mapData.data!!.battleRoyale.current.map
                                binding.nextMapName.text = mapData.data.battleRoyale.next.map
                                appViewModel.hideSplash()
                                requireActivity().dataStore.edit {
                                    it[NEXT_MAP] = mapData.data.battleRoyale.next.map
                                }
                            }
                        }
                    }
                }
                launch {
                    appViewModel.showMenu.collectLatest { showMenu ->
                        when (showMenu) {
                            true -> showMenu()
                            false -> hideMenu()
                        }
                    }
                }
            }
        }
    }

    private fun hideMenu() {
        notifyButton.isClickable = false
        alarmButton.isClickable = false
        foregroundViews.forEach {
            ObjectAnimator.ofFloat(it, "alpha", 1.0f).apply {
                interpolator = LinearInterpolator()
                duration = 600
                start()
            }
        }
        backgroundViews.forEach {
            ObjectAnimator.ofFloat(it, "alpha", 0.0f).apply {
                interpolator = LinearInterpolator()
                duration = 600
                start()
            }
        }
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
    }

    private fun showMenu() {
        notifyButton.isClickable = true
        alarmButton.isClickable = true
        foregroundViews.forEach {
            ObjectAnimator.ofFloat(it, "alpha", 0.0f).apply {
                interpolator = LinearInterpolator()
                duration = 600
                start()
            }
        }
        backgroundViews.forEach {
            ObjectAnimator.ofFloat(it, "alpha", 1.0f).apply {
                interpolator = LinearInterpolator()
                duration = 600
                start()
            }
        }
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
    }

    private fun setupAnimationListeners() {
        binding.alarmButton.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
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
                    requireContext().cancelAlert(true)
                }
            }

            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        })
        binding.notifyButton.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
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
                    requireContext().cancelAlert(false)
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
    }

    override fun onSwipeLeft() {
        navController.navigate(BattleRoyalFragmentDirections.actionBattleRoyalFragmentToArenasFragment())
    }

    override fun onSwipeRight() {}
}