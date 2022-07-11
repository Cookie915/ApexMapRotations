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
import com.example.apexmaprotations.util.dataStore
import com.example.apexmaprotations.viewmodels.ApexViewModel
import com.example.apexmaprotations.viewmodels.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "BattleRoyalFragment"

@AndroidEntryPoint
class BattleRoyalFragment @Inject constructor(
    var apexViewModel: ApexViewModel?,
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
            viewmodel = apexViewModel
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

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "Resumed")
        apexViewModel?.checkTimers()
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
        apexViewModel = apexViewModel
            ?: ViewModelProvider(requireActivity())[ApexViewModel::class.java]
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
                    apexViewModel?.mapDataBundle?.collect { mapDataBundleResponse ->
                        when (mapDataBundleResponse) {
                            is NetworkResult.Loading -> {
                                Log.i("tesss", "Loading")
                            }
                            is NetworkResult.Error -> {
                                appViewModel?.hideSplash()
                                Log.i(
                                    TAG,
                                    "MapDataBundleError: " + mapDataBundleResponse.message.toString()
                                )
                                when (mapDataBundleResponse.message) {
                                    "429" -> {
                                        appViewModel?.showError("Too many request, please wait a few seconds and try again")
                                    }
                                    "Api call failed java.security.cert.CertPathValidatorException: Trust anchor for certification path not found." -> {
                                        appViewModel?.showError("Insecure Connection, connect to a new network and try again")
                                    }
                                    "Api call failed Unable to resolve host \"api.mozambiquehe.re\": No address associated with hostname" -> {
                                        appViewModel?.showError("Unable to connect to server, check connection and try again")
                                    }
                                    else -> {
                                        appViewModel?.showError(
                                            mapDataBundleResponse.message
                                                ?: "An unknown error occurred"
                                        )
                                    }
                                }
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
                    apexViewModel?.timeRemainingBr?.map {
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
        binding.setAlarmBtn.setOnClickListener {

        }
    }

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