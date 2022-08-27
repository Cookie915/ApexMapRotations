package com.cooksmobilesolutions.apexmaprotations.fragments.introScreens

import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cooksmobilesolutions.apexmaprotations.R

class RefreshDataIntroFragment : Fragment(R.layout.fragment_refreshdata_intro) {
    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<TextView>(R.id.IntroScreenTextView)
            .setText(R.string.refreshDataIntroText)
    }
}