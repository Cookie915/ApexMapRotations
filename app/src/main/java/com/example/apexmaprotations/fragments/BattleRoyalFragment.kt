package com.example.apexmaprotations.fragments

import android.app.AlarmManager
import android.content.Context.ALARM_SERVICE
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.apexmaprotations.R
import com.example.apexmaprotations.databinding.FragmentBattleroyaleBinding
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.util.NEXT_MAP
import com.example.apexmaprotations.util.SwipeGestureListener
import com.example.apexmaprotations.util.SwipeListener
import com.example.apexmaprotations.viewmodels.AppViewModel
import com.example.apexmaprotations.viewmodels.ArenasViewModel
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel
import com.example.apexmaprotations.viewmodels.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "BattleRoyalFragment"

@AndroidEntryPoint
class BattleRoyalFragment @Inject constructor(
    var battleRoyalViewModel: BattleRoyalViewModel?,
    var arenasViewModel: ArenasViewModel?,
    var appViewModel: AppViewModel?,
) : Fragment(R.layout.fragment_battleroyale), SwipeListener {
    private val transitionInflater: TransitionInflater by lazy {
        TransitionInflater.from(
            requireContext()
        )
    }
    private val binding: FragmentBattleroyaleBinding by lazy {
        FragmentBattleroyaleBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@BattleRoyalFragment
            viewmodel = battleRoyalViewModel
        }
    }
    private val alarmManager: AlarmManager by lazy {
        requireActivity().getSystemService(
            ALARM_SERVICE
        ) as AlarmManager
    }
    private val foregroundViews by lazy {
        listOf(
            binding.timerAnimation,
            binding.time,
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = transitionInflater.inflateTransition(R.transition.slide_left)
        // fix
        // verifyAlerts()
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
        battleRoyalViewModel = battleRoyalViewModel
            ?: ViewModelProvider(this)[BattleRoyalViewModel::class.java]
//        arenasViewModel =
//            arenasViewModel ?: ViewModelProvider(this)[ArenasViewModel::class.java]
        appViewModel =
            appViewModel ?: ViewModelProvider(requireActivity())[AppViewModel::class.java]
//        setUpClickListeners()
//        setupAnimationListeners()
//        listenToAlarms()
//        lottieListeners()
        setupObservables()
        setupListeners()
    }

    private fun setupObservables() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    battleRoyalViewModel?.mapDataBundle?.collect { mapDataBundleResponse ->
                        when (mapDataBundleResponse) {
                            is NetworkResult.Loading -> {
                                Log.i("tesss", "Loading")
                            }
                            is NetworkResult.Error -> {
                                Log.i("tesss", "Err")
                            }
                            is NetworkResult.Success -> {
                                Log.i("tesss", "Succ")
                                appViewModel?.hideSplash()
                                requireActivity().dataStore.edit {
                                    it[NEXT_MAP] =
                                        mapDataBundleResponse.data?.battleRoyale?.next?.map.toString()
                                }
                                binding.currentMapText.text =
                                    getString(R.string.currentMapText) + " " +
                                            mapDataBundleResponse.data?.battleRoyale?.current?.map
                                binding.nextMapText.text = getString(R.string.nextUpText) + " " +
                                        mapDataBundleResponse.data?.battleRoyale?.next?.map
                            }
                        }
                    }
                }
                launch {
                    battleRoyalViewModel?.timeRemaining?.map {
                        binding.time.text = getString(
                            R.string.time_format_br,
                            it[0].toString().toLong(),
                            it[1].toString(),
                            it[2].toString(),
                            it[3].toString().toLong()
                        )
                    }?.collect()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.root.setOnTouchListener(SwipeGestureListener(this))
        binding.rightArrow.setOnClickListener {
            findNavController().navigate(BattleRoyalFragmentDirections.actionBattleRoyalFragmentToArenasFragment())
        }
    }

//    private fun setupObservables() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                launch {
//                    battleRoyalViewModel?.currentMapImage?.collectLatest {
//                        if (it != null) {
//                            binding.currentMapImage.setImageDrawable(
//                                requireActivity().getDrawable(it)
//                            )
//                        }
//                    }
//                }
//                launch {
//                    battleRoyalViewModel?.nextMapImage?.collectLatest {
//                        if (it != null) {
//                            binding.nextMapImage.setImageDrawable(requireActivity().getDrawable(it))
//                        }
//                    }
//                }
//                launch {
//                    battleRoyalViewModel?.timeRemaining?.map {
//                        binding.time.text = getString(
//                            R.string.time_value_format,
//                            it[0].toString().toLong(),
//                            it[1].toString(),
//                            it[2].toString(),
//                            it[3].toString().toLong()
//                        )
//                    }?.collect()
//                }
//                launch {
//                    battleRoyalViewModel?.mapDataBundle?.collectLatest { mapData ->
//                        when (mapData) {
//                            is NetworkResult.Loading -> {
//                                Log.i(TAG, "mapdata Loading from setupobvs()")
//                                Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT)
//                                    .show()
//                                //todo show loading
//                            }
//                            is NetworkResult.Error -> {
//                                Log.i(TAG, "mapdata Error from setupobvs()")
//                                Toast.makeText(
//                                    requireContext(),
//                                    "MapData Failed ${mapData.message}",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                            is NetworkResult.Success -> {
//                                Log.i(TAG, "mapdata Success from setupobvs()")
//                                binding.currentMapName.text =
//                                    mapData.data!!.battleRoyale.current.map
//                                binding.nextMapName.text = mapData.data.battleRoyale.next.map
//                                appViewModel?.hideSplash()
//                                requireActivity().dataStore.edit {
//                                    it[NEXT_MAP] = mapData.data.battleRoyale.next.map
//                                }
//                            }
//                        }
//                    }
//                }
//                launch {
//                    appViewModel?.showMenu?.collectLatest { showMenu ->
//                        when (showMenu) {
//                            true -> showMenu()
//                            false -> hideMenu()
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun hideMenu() {
//        notifyButton.isClickable = false
//        alarmButton.isClickable = false
//        foregroundViews.forEach {
//            ObjectAnimator.ofFloat(it, "alpha", 1.0f).apply {
//                interpolator = LinearInterpolator()
//                duration = 600
//                start()
//            }
//        }
//        backgroundViews.forEach {
//            ObjectAnimator.ofFloat(it, "alpha", 0.0f).apply {
//                interpolator = LinearInterpolator()
//                duration = 600
//                start()
//            }
//        }
//        ObjectAnimator.ofFloat(
//            binding.menuBackgroundLines,
//            "translationY",
//            -linesOffset
//        ).apply {
//            duration = 800
//            start()
//        }
//        ObjectAnimator.ofFloat(binding.menubackground, "alpha", 0f)
//            .apply {
//                interpolator = LinearInterpolator()
//                duration = 800
//                start()
//            }
//    }
//
//    private fun showMenu() {
//        notifyButton.isClickable = true
//        alarmButton.isClickable = true
//        foregroundViews.forEach {
//            ObjectAnimator.ofFloat(it, "alpha", 0.0f).apply {
//                interpolator = LinearInterpolator()
//                duration = 600
//                start()
//            }
//        }
//        backgroundViews.forEach {
//            ObjectAnimator.ofFloat(it, "alpha", 1.0f).apply {
//                interpolator = LinearInterpolator()
//                duration = 600
//                start()
//            }
//        }
//        ObjectAnimator.ofFloat(
//            binding.menuBackgroundLines,
//            "translationY",
//            0f
//        )
//            .apply {
//                duration = 800
//                start()
//            }
//        ObjectAnimator.ofFloat(binding.menubackground, "alpha", 0.8f)
//            .apply {
//                interpolator = LinearInterpolator()
//                duration = 800
//                start()
//            }
//    }
//
//    private fun setupAnimationListeners() {
//        binding.alarmButton.addAnimatorListener(object : Animator.AnimatorListener {
//            override fun onAnimationEnd(animation: Animator?) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    if (!alarmManager.canScheduleExactAlarms()) {
//                        val dialog = PermissionDialogFragment()
//                        dialog.show(
//                            requireActivity().supportFragmentManager,
//                            "PermissionDialog"
//                        )
//                    }
//                }
//                if (binding.alarmButton.progress > 0) {
//                    battleRoyalViewModel?.timeRemainingLong?.let {
//                        requireContext().scheduleNotification(
//                            true,
//                            it.value
//                        )
//                    }
//                } else {
//                    binding.notifyButton.isEnabled = true
//                    requireContext().cancelAlert(true)
//                }
//            }
//
//            override fun onAnimationStart(animation: Animator?) {}
//            override fun onAnimationCancel(animation: Animator?) {}
//            override fun onAnimationRepeat(animation: Animator?) {}
//        })
//        binding.notifyButton.addAnimatorListener(object : Animator.AnimatorListener {
//            override fun onAnimationEnd(animation: Animator?) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    if (!alarmManager.canScheduleExactAlarms()) {
//                        val dialog = PermissionDialogFragment()
//                        dialog.show(
//                            requireActivity().supportFragmentManager,
//                            "PermissionDialog"
//                        )
//                    }
//                }
//                if (binding.notifyButton.progress > 0) {
//                    battleRoyalViewModel?.timeRemainingLong?.let {
//                        requireContext().scheduleNotification(
//                            false,
//                            it.value
//                        )
//                    }
//                } else {
//                    binding.alarmButton.isEnabled = true
//                    requireContext().cancelAlert(false)
//                }
//            }
//
//            override fun onAnimationStart(animation: Animator?) {}
//            override fun onAnimationCancel(animation: Animator?) {}
//            override fun onAnimationRepeat(animation: Animator?) {}
//        })
//    }
//
//    private fun setUpClickListeners() {
//        val notifyButton = binding.notifyButton
//        val alarmButton = binding.alarmButton
//        val menubackground = binding.menubackground
//        notifyButton.setOnClickListener {
//            if (notifyButton.progress == 0f) {
//                notifyButton.speed = 1f
//                notifyButton.playAnimation()
//            } else {
//                notifyButton.speed = -1f
//                notifyButton.playAnimation()
//            }
//        }
//        alarmButton.setOnClickListener {
//            if (alarmButton.progress == 0f) {
//                alarmButton.speed = 1f
//                alarmButton.playAnimation()
//            } else {
//                alarmButton.speed = -1f
//                alarmButton.playAnimation()
//            }
//        }
//        menubackground.setOnTouchListener(SwipeGestureListener(this))
//        binding.arrowRight.setOnClickListener {
//            findNavController().navigate(BattleRoyalFragmentDirections.actionBattleRoyalFragmentToArenasFragment())
//        }
//        menubackground.setOnLongClickListener {
//            when (appViewModel!!.showMenu.value) {
//                true -> {
//                    appViewModel?.hideMenu()
//                }
//                false -> {
//                    appViewModel!!.showMenu()
//                }
//            }
//            return@setOnLongClickListener true
//        }
//
//
//    }
//
//    private fun lottieListeners() {
//        notifyButton.addLottieOnCompositionLoadedListener {
//            notifyButton.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER) {
//                if (alarmButton.progress > 0f) PorterDuffColorFilter(
//                    Color.DKGRAY,
//                    PorterDuff.Mode.DARKEN
//                ) else SimpleColorFilter(Color.TRANSPARENT)
//            }
//        }
//        alarmButton.addLottieOnCompositionLoadedListener {
//            alarmButton.addValueCallback(KeyPath("**"), LottieProperty.COLOR_FILTER) {
//                if (notifyButton.progress > 0f) PorterDuffColorFilter(
//                    Color.DKGRAY,
//                    PorterDuff.Mode.DARKEN
//                ) else SimpleColorFilter(Color.TRANSPARENT)
//            }
//        }
//    }
//
//    private fun listenToAlarms() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                requireActivity().dataStore.data.map {
//                    val alarmTime = it[ALARM_TIME] ?: 0L
//                    val notifyTime = it[NOTIFICATION_TIME] ?: 0L
//                    if (alarmTime == 0L) {
//                        binding.alarmButton.progress = 0f
//                    } else {
//                        binding.notifyButton.isEnabled = false
//                        binding.alarmButton.progress = 1f
//                    }
//                    if (notifyTime == 0L) {
//                        binding.notifyButton.progress = 0f
//                    } else {
//                        binding.alarmButton.isEnabled = false
//                        binding.notifyButton.progress = 1f
//                    }
//                    if (alarmTime == 0L && notifyTime == 0L) {
//                        binding.notifyButton.isEnabled = true
//                        binding.alarmButton.isEnabled = true
//                    }
//                }.collect()
//            }
//        }
//    }

    override fun onSwipeLeft() {
        Log.i("tesss", "left")
        findNavController().navigate(BattleRoyalFragmentDirections.actionBattleRoyalFragmentToArenasFragment())
    }

    override fun onSwipeRight() {}
}