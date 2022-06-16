package com.example.apexmaprotations.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.apexmaprotations.R
import com.example.apexmaprotations.databinding.FragmentArenasBinding
import com.example.apexmaprotations.util.SwipeListener
import com.example.apexmaprotations.viewmodels.ArenasViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ArenasFragment : Fragment(R.layout.fragment_arenas), SwipeListener {
    private lateinit var navController: NavController
    private val arenasViewModel: ArenasViewModel by viewModels({ requireActivity() })
    private val binding: FragmentArenasBinding by lazy {
        FragmentArenasBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Glide.with(this)
            .load(R.drawable.encore)
            .diskCacheStrategy(
                DiskCacheStrategy.ALL
            )
            .into(binding.rankedCurrent)
        Glide.with(this)
            .load(R.drawable.habitat)
            .into(binding.rankedNext)
        Glide.with(this)
            .load(R.drawable.party_crash)
            .into(binding.publicCurrent)
        Glide.with(this)
            .load(R.drawable.phase_rush)
            .into(binding.publicNext)
        binding.timerTop.doOnPreDraw {
            //  Manually set X to avoid jitter caused by centering text as decimal place in timer changes
            val viewWidth = it.width / 2
            val width = resources.displayMetrics.widthPixels / 2 - viewWidth
            binding.timerTop.translationX += width
        }
        binding.timerBottom.doOnPreDraw {
            //  Manually set X to avoid jitter caused by centering text as decimal place in timer changes
            val viewWidth = it.width / 2
            val width = resources.displayMetrics.widthPixels / 2 - viewWidth
            binding.timerBottom.translationX += width
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        val transitionInflater = TransitionInflater.from(requireContext())
        enterTransition = transitionInflater.inflateTransition(R.transition.slide_right)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setUpObservables()
    }


    private fun setUpObservables() {
        lifecycleScope.launchWhenResumed {
            launch {
                arenasViewModel.mapDataBundle.collect()
            }
            launch {
                Log.i("tester2", "test")
                arenasViewModel.timeRemainingRanked.map {
                    binding.timerTop.text = getString(
                        R.string.time_format_arenas,
                        it[0].toString(),
                        it[1].toString(),
                        it[2].toString().toLong()
                    )
                }.collect()
            }
            launch {
                arenasViewModel.timeRemainingUnranked.map {
                    binding.timerBottom.text = getString(
                        R.string.time_format_arenas,
                        it[0].toString(),
                        it[1].toString(),
                        it[2].toString().toLong()
                    )
                }.collect()
            }
        }
    }

    override fun onSwipeLeft() {

    }

    override fun onSwipeRight() {
        navController.popBackStack()
    }
}

