package com.example.apexmaprotations.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import javax.inject.Inject

class ApexFragmentFactory @Inject constructor(
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            ArenasFragment::class.java.name -> ArenasFragment(null, null)
            BattleRoyalFragment::class.java.name -> BattleRoyalFragment(null, null)
            else -> return super.instantiate(classLoader, className)
        }
    }
}