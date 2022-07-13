package com.example.apexmaprotations.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.example.apexmaprotations.R
import com.example.apexmaprotations.databinding.FragmentArenasBinding
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.util.SwipeGestureListener
import com.example.apexmaprotations.util.SwipeListener
import com.example.apexmaprotations.util.capitalizeWords
import com.example.apexmaprotations.viewmodels.ApexViewModel
import com.example.apexmaprotations.viewmodels.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ArenasFragmentLogs"
@AndroidEntryPoint
class ArenasFragment @Inject constructor(
    var apexViewModel: ApexViewModel?,
    var appViewModel: AppViewModel?
) : Fragment(), SwipeListener {
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
        //  Use real viewmodel in production, but allow us to inject fake viewmodels for tests
        apexViewModel =
            apexViewModel ?: ViewModelProvider(requireActivity())[ApexViewModel::class.java]
        appViewModel =
            appViewModel ?: ViewModelProvider(requireActivity())[AppViewModel::class.java]
        setupListeners()
        setupObservables()
    }

    override fun onResume() {
        super.onResume()
//        lifecycleScope.launch {
//            apexViewModel?.refreshMapData()
//        }
        Log.i("ApexRepo", "onResume")
//        apexViewModel?.checkTimers()
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }

    private fun setupListeners() {
        binding.root.setOnTouchListener(SwipeGestureListener(this))
    }

    private fun setupObservables() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    apexViewModel?.mapDataBundle?.collect {
                        when (it) {
                            is NetworkResult.Loading -> {
                            }
                            is NetworkResult.Error -> {
                            }
                            is NetworkResult.Success -> {
                                Log.i("ApexRepo", "${it.data!!.battleRoyale.current.remainingSecs}")
                                apexViewModel?.initializeTimer(it.data)
                                binding.currentText.text =
                                    getString(R.string.currentMapText) + " " +
                                            it.data?.arenas?.current?.map?.capitalizeWords()
                                binding.nextText.text = getString(R.string.nextUpText) + " " +
                                        it.data?.arenas?.next?.map?.capitalizeWords()
                                binding.rankedCurrentText.text =
                                    getString(R.string.currentMapText) + " " +
                                            it.data?.arenasRanked?.current?.map?.capitalizeWords()
                                binding.rankedNextText.text = getString(R.string.nextUpText) + " " +
                                        it.data?.arenasRanked?.next?.map?.capitalizeWords()
                                apexViewModel?.refreshMapData()
                            }
                        }
                    }
                }
                launch {
                    apexViewModel?.timeRemainingArenas?.map {
                        binding.timer.text = getString(
                            R.string.time_format_arenas,
                            it[0].toString(),
                            it[1].toString(),
                            it[2].toString().toLong()
                        )
                    }?.collect()
                    1
                }
                launch {
                    apexViewModel?.timeRemainingArenasRanked?.map {
                        binding.rankedTimer.text = getString(
                            R.string.time_format_arenas,
                            it[0].toString(),
                            it[1].toString(),
                            it[2].toString().toLong()
                        )
                    }?.collect()
                }
            }
        }
    }

    override fun onSwipeLeft() {}

    override fun onSwipeRight() {
        view?.findNavController()?.popBackStack()
    }
}

