package com.example.apexmaprotations.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.example.apexmaprotations.R
import com.example.apexmaprotations.data.models.NetworkResult
import com.example.apexmaprotations.databinding.FragmentBattleroyaleBinding
import com.example.apexmaprotations.viewmodels.ApexViewModel
import com.example.apexmaprotations.viewmodels.AppViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*


private const val TAG = "BattleRoyalFragment"

@AndroidEntryPoint
class BattleRoyalFragment : Fragment(R.layout.fragment_battleroyale) {
    val apexViewModel: ApexViewModel by viewModels({ requireActivity() })
    val appViewModel: AppViewModel by viewModels({ requireActivity() })

    private val binding: FragmentBattleroyaleBinding by lazy {
        FragmentBattleroyaleBinding.inflate(layoutInflater).apply {
            viewmodel = apexViewModel
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("tester13", "br frag created")
        setupListeners()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val transitionInflater = TransitionInflater.from(requireContext())
        enterTransition = transitionInflater.inflateTransition(R.transition.slide_right)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<ImageView>(R.id.appIconMain)
            ?.let { it.visibility = View.VISIBLE }
//        binding.lifecycleOwner = this
        lifecycleScope.launch {
            if (apexViewModel.mapDataBundle.value == null) {
                apexViewModel.refreshMapData()
            }
        }
        setupObservables()
    }

    override fun onResume() {
        super.onResume()
        apexViewModel.checkTimers(Calendar.getInstance())
    }

    private fun setupObservables() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    apexViewModel.mapDataBundle.collect { mapDataBundleResponse ->
                        if (mapDataBundleResponse != null) {
                            when (mapDataBundleResponse) {
                                is NetworkResult.Loading -> {
                                    Log.i(TAG, "Loading....")
                                }
                                is NetworkResult.Error -> {
                                    appViewModel.hideSplash()
                                    Log.i(
                                        TAG,
                                        "MapDataBundleError: " + mapDataBundleResponse.message.toString()
                                    )
                                    when (mapDataBundleResponse.message) {
                                        "429" -> {
                                            appViewModel.showError("Too many request, please wait a few seconds and try again")
                                        }
                                        "Api call failed java.security.cert.CertPathValidatorException: Trust anchor for certification path not found." -> {
                                            appViewModel.showError("Insecure Connection, connect to a new network and try again")
                                        }
                                        "Api call failed Unable to resolve host \"api.mozambiquehe.re\": No address associated with hostname" -> {
                                            appViewModel.showError("Unable to connect to server, check connection and try again")
                                        }
                                        else -> {
                                            appViewModel.showError(
                                                mapDataBundleResponse.message
                                                    ?: "An unknown error occurred"
                                            )
                                        }
                                    }
                                }
                                is NetworkResult.Success -> {
                                    Log.i(TAG, "Success")
                                    appViewModel.hideSplash()
                                    appViewModel.resetErrorState()
                                    apexViewModel.setNextMapPreferences(mapDataBundleResponse.data?.battleRoyale?.next?.map.toString())
                                    binding.currentMapText.text =
                                        getString(R.string.currentMapText) + " " +
                                                mapDataBundleResponse.data?.battleRoyale?.current?.map
                                    binding.nextMapText.text =
                                        getString(R.string.nextUpText) + " " +
                                                mapDataBundleResponse.data?.battleRoyale?.next?.map
                                }
                            }
                        }
                    }
                }
                launch {
                    apexViewModel.timeRemainingBr.map {
                        binding.time.text = getString(
                            R.string.time_format_br,
                            it[0].toString().toLong(),
                            it[1].toString(),
                            it[2].toString(),
                            it[3].toString().toLong()
                        )
                    }.collect()
                }
                launch {
                    appViewModel.isAlarmSet.collect {
                        when (it) {
                            true -> {
                                binding.setNotificationButton.alpha = 0.7f
                                binding.setNotificationText.alpha = 0.7f
                                binding.setNotificationButton.isClickable = false
                                binding.setAlarmBtn.setImageDrawable(requireContext().getDrawable(R.drawable.ic_alarm_off))
                            }
                            false -> {
                                binding.setNotificationButton.alpha = 1f
                                binding.setNotificationText.alpha = 1f
                                binding.setNotificationButton.isClickable = true
                                binding.setAlarmBtn.setImageDrawable(requireContext().getDrawable(R.drawable.ic_alarm))
                            }
                        }
                    }
                }
                launch {
                    appViewModel.isNotificationSet.collect {
                        when (it) {
                            true -> {
                                binding.setAlarmBtn.alpha = 0.7f
                                binding.setAlarmText.alpha = 0.7f
                                binding.setAlarmBtn.isClickable = false
                                binding.setNotificationButton.setImageDrawable(
                                    requireContext().getDrawable(
                                        R.drawable.ic_notification_off
                                    )
                                )
                            }
                            false -> {
                                binding.setAlarmBtn.alpha = 1f
                                binding.setAlarmText.alpha = 1f
                                binding.setAlarmBtn.isClickable = true
                                binding.setNotificationButton.setImageDrawable(
                                    requireContext().getDrawable(
                                        R.drawable.ic_notification
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.rightArrow.setOnClickListener {
            requireActivity().findViewById<ViewPager2>(R.id.viewPager).currentItem = 1
        }
        binding.setAlarmBtn.setOnClickListener {
            when (appViewModel.isAlarmSet.value) {
                true -> {
                    Snackbar.make(requireView(), "Alarm Cancelled", Snackbar.LENGTH_SHORT).show()
                    appViewModel.cancelAlert(requireContext(), true)
                }
                false -> {
                    Snackbar.make(requireView(), "Alarm Set", Snackbar.LENGTH_SHORT).show()
                    if (apexViewModel.mapDataBundle.value != null) {
                        apexViewModel.mapDataBundle.value?.data?.battleRoyale?.current?.remainingSecs?.toLong()
                            ?.let { timeRemaining ->
                                appViewModel.scheduleNotification(
                                    requireContext(),
                                    true,
                                    timeRemaining * 1000L
                                )
                            }
                    }
                }
            }
        }
        binding.setNotificationButton.setOnClickListener {
            when (appViewModel.isNotificationSet.value) {
                true -> {
                    Snackbar.make(requireView(), "Notification Cancelled", Snackbar.LENGTH_SHORT)
                        .show()
                    appViewModel.cancelAlert(requireContext(), false)
                }
                false -> {
                    Snackbar.make(requireView(), "Notification Set", Snackbar.LENGTH_SHORT).show()
                    if (apexViewModel.mapDataBundle.value != null) {
                        apexViewModel.mapDataBundle.value?.data?.battleRoyale?.current?.remainingSecs?.toLong()
                            ?.let { timeRemaining ->
                                appViewModel.scheduleNotification(
                                    requireContext(),
                                    false,
                                    timeRemaining * 1000L
                                )
                            }
                    }
                }
            }
        }
    }
}