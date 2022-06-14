package com.example.apexmaprotations.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.apexmaprotations.R
import com.example.apexmaprotations.databinding.FragmentArenasBinding
import com.example.apexmaprotations.viewmodels.ArenasViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ArenasFragment : Fragment(R.layout.fragment_arenas) {
    private val arenasViewModel: ArenasViewModel by viewModels({ requireActivity() })
    private val binding: FragmentArenasBinding by lazy {
        FragmentArenasBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.topTimer.doOnPreDraw {
            //  Manually set X to avoid jitter caused by centering text as decimal place in timer changes
            val viewWidth = it.width / 2
            val width = resources.displayMetrics.widthPixels / 2 - viewWidth
            binding.topTimer.translationX += width
        }
        binding.bottomTimer.doOnPreDraw {
            //  Manually set X to avoid jitter caused by centering text as decimal place in timer changes
            val viewWidth = it.width / 2
            val width = resources.displayMetrics.widthPixels / 2 - viewWidth
            binding.bottomTimer.translationX += width
        }
        Log.i("tester2", "Created")


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
        setUpObservables()
    }


    private fun setUpObservables() {
        lifecycleScope.launchWhenCreated {

            launch {
                Log.i("tester2", "test")
                arenasViewModel.timeRemainingRanked.map {
                    binding.topTimer.text = getString(
                        R.string.time_format_arenas,
                        it[0].toString(),
                        it[1].toString(),
                        it[2].toString().toLong()
                    )
                }.collect()
            }
            launch {
                arenasViewModel.timeRemainingUnranked.map {
                    binding.bottomTimer.text = getString(
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

