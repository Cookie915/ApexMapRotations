package com.example.apexmaprotations.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.apexmaprotations.R
import com.example.apexmaprotations.databinding.FragmentArenasBinding
import com.example.apexmaprotations.viewmodels.ArenasViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ArenasFragment : Fragment(R.layout.fragment_arenas) {
    private val arenasViewModel: ArenasViewModel by viewModels()
    private val binding: FragmentArenasBinding by lazy {
        FragmentArenasBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}