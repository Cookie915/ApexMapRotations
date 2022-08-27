package com.cooksmobilesolutions.apexmaprotations.fragments

import android.os.Bundle
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
import com.cooksmobilesolutions.apexmaprotations.R
import com.cooksmobilesolutions.apexmaprotations.data.models.NetworkResult
import com.cooksmobilesolutions.apexmaprotations.databinding.FragmentArenasBinding
import com.cooksmobilesolutions.apexmaprotations.util.capitalizeWords
import com.cooksmobilesolutions.apexmaprotations.viewmodels.ApexViewModel
import com.cooksmobilesolutions.apexmaprotations.viewmodels.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ArenasFragment : Fragment() {
    val apexViewModel: ApexViewModel by viewModels({ requireActivity() })
    val appViewModel: AppViewModel by viewModels({ requireActivity() })

    private val binding: FragmentArenasBinding by lazy {
        FragmentArenasBinding.inflate(layoutInflater)
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
        shouldShowAppIcon()
        lifecycleScope.launch {
            if (apexViewModel.mapDataBundle.value == null) {
                apexViewModel.refreshMapData()
            }
        }
        setupObservables()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        apexViewModel.checkTimers(Calendar.getInstance())
    }

    private fun shouldShowAppIcon() {
        val maxAvailableHeight = requireActivity().resources.displayMetrics.heightPixels
        val maxAvailableHeightDp =
            maxAvailableHeight / requireActivity().resources.displayMetrics.density
        if (maxAvailableHeightDp <= 480) {
            val appIcon = requireActivity().findViewById<ImageView>(R.id.appIconMain)
            appIcon?.let {
                it.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.leftArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupObservables() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    apexViewModel.mapDataBundle.collect { mapDataBundleResponse ->
                        if (mapDataBundleResponse != null) {
                            when (mapDataBundleResponse) {
                                is NetworkResult.Loading -> {
                                }
                                is NetworkResult.Error -> {
                                    appViewModel.hideSplash()
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
                                    appViewModel.hideSplash()
                                    appViewModel.resetErrorState()
                                    apexViewModel.setNextMapPreferences(mapDataBundleResponse.data!!.brCurrentMapName)
                                    binding.currentText.text = getString(
                                        R.string.currentMapText,
                                        mapDataBundleResponse.data.arenasCurrentMapName.capitalizeWords()
                                    )
                                    binding.nextText.text = getString(
                                        R.string.nextMapText,
                                        mapDataBundleResponse.data.arenasNextMapName.capitalizeWords()
                                    )
                                    binding.rankedCurrentText.text = getString(
                                        R.string.currentMapText,
                                        mapDataBundleResponse.data.rankedArenasCurrentMapName.capitalizeWords()
                                    )
                                    binding.rankedNextText.text = getString(
                                        R.string.nextMapText,
                                        mapDataBundleResponse.data.rankedArenasNextMapName.capitalizeWords()
                                    )
                                }
                            }
                        }
                    }
                }
                launch {
                    apexViewModel.timeRemainingArenas.map {
                        binding.timer.text = getString(
                            R.string.time_format_arenas,
                            it[0].toString(),
                            it[1].toString(),
                            it[2].toString().toLong()
                        )
                    }.collect()
                }
                launch {
                    apexViewModel.timeRemainingArenasRanked.map {
                        binding.rankedTimer.text = getString(
                            R.string.time_format_arenas,
                            it[0].toString(),
                            it[1].toString(),
                            it[2].toString().toLong()
                        )
                    }.collect()
                }
            }
        }
    }
}

