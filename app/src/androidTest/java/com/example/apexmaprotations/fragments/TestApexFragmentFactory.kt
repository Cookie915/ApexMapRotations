package com.example.apexmaprotations.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.example.apexmaprotations.repo.FakeApexRepo
import com.example.apexmaprotations.viewmodels.AppViewModel
import com.example.apexmaprotations.viewmodels.ArenasViewModel
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel
import javax.inject.Inject

class TestApexFragmentFactory @Inject constructor(
) : FragmentFactory() {
    val fakeRepo = FakeApexRepo()
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            ArenasFragment::class.java.name ->
                ArenasFragment(ArenasViewModel(fakeRepo))

            BattleRoyalFragment::class.java.name ->
                BattleRoyalFragment(
                    BattleRoyalViewModel(fakeRepo),
                    ArenasViewModel(fakeRepo),
                    AppViewModel()
                )

            else -> return super.instantiate(classLoader, className)
        }
    }
}