package com.example.apexmaprotations.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.apexmaprotations.R
import com.example.apexmaprotations.databinding.FragmentArenasBinding
import com.example.apexmaprotations.util.SwipeGestureListener
import com.example.apexmaprotations.util.SwipeListener
import com.example.apexmaprotations.viewmodels.ArenasViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ArenasFragment : Fragment(R.layout.fragment_arenas), SwipeListener {
    private lateinit var navController: NavController
    private val arenasViewModel: ArenasViewModel by activityViewModels()
    private val binding: FragmentArenasBinding by lazy {
        FragmentArenasBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  We center timers here to avoid jitter from centering text as time changes
        binding.timerTop.doOnPreDraw {
            val viewWidth = it.width / 2
            val width = resources.displayMetrics.widthPixels / 2 - viewWidth
            binding.timerTop.translationX += width
        }
        binding.timerBottom.doOnPreDraw {
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
        setUpObservables()
        assignMapImages()
        binding.background.setOnTouchListener(SwipeGestureListener(this))
    }

    override fun onResume() {
        super.onResume()

    }


    private fun setUpObservables() {
        lifecycleScope.launchWhenCreated {
            launch {
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

//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        assignMapImages()
//        setUpObservables()
//    }

    private fun assignMapImages() {
        lifecycleScope.launchWhenCreated {
            launch {
                arenasViewModel.currentMapImageRanked.collect {
                    if (it != null) {
                        Glide.with(this@ArenasFragment)
                            .load(it)
                            .signature(ObjectKey(it))
                            .into(binding.rankedCurrent)
                    }
                }
            }
            launch {
                arenasViewModel.nextMapImageRanked.collect {
                    if (it != null) {
                        Glide.with(this@ArenasFragment)
                            .load(requireContext().getDrawable(it))
                            .signature(ObjectKey(it))
                            .into(binding.rankedNext)
                    }
                }
            }
            launch {
                arenasViewModel.currentMapImage.collect {
                    if (it != null) {
                        Glide.with(this@ArenasFragment)
                            .load(requireContext().getDrawable(it))
                            .signature(ObjectKey(it))
                            .into(binding.publicCurrent)
                    }
                }
            }
            launch {
                arenasViewModel.nextMapImage.collect {
                    if (it != null) {
                        Glide.with(this@ArenasFragment)
                            .load(requireContext().getDrawable(it))
                            .signature(ObjectKey(it))
                            .into(binding.publicNext)
                    }
                }
            }
        }
    }

    override fun onSwipeLeft() {}

    override fun onSwipeRight() {
        navController.popBackStack()
    }
}

