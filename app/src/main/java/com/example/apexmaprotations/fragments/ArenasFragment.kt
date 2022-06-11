package com.example.apexmaprotations.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.apexmaprotations.R
import com.example.apexmaprotations.viewmodels.ArenasViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ArenasFragment : Fragment(R.layout.fragment_arenas) {
    private val arenasViewModel: ArenasViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
}