package com.example.apexmaprotations

import com.example.apexmaprotations.fragments.ArenasFragmentTest
import com.example.apexmaprotations.fragments.BattleRoyalFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(Suite::class)
@Suite.SuiteClasses(
    ArenasFragmentTest::class,
    BattleRoyalFragment::class
)
class AndroidTestSuite