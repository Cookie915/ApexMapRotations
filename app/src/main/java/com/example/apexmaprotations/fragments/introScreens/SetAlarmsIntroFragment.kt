package com.example.apexmaprotations.fragments.introScreens

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.apexmaprotations.R

class SetAlarmsIntroFragment : Fragment(R.layout.fragment_setalarms_intro) {
    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<TextView>(R.id.IntroScreenTextView)
            .setText(R.string.setAlarmsIntroText)
        requireActivity().findViewById<Button>(R.id.finishIntroButton).visibility = View.VISIBLE
    }
}