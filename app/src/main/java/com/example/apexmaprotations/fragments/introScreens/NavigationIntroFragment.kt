package com.example.apexmaprotations.fragments.introScreens

import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.apexmaprotations.R

class NavigationIntroFragment : Fragment(R.layout.fragment_navigation_intro) {
    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<TextView>(R.id.IntroScreenTextView)
            .setText(R.string.navigationIntroText)
    }
}