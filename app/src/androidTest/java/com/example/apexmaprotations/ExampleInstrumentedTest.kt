package com.example.apexmaprotations

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.apexmaprotations.hilt.RetroFitModule
import com.example.apexmaprotations.repo.ApexRepo
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: ApexRepo
    private lateinit var viewModel: BattleRoyalViewModel

    @Before
    fun setUp() {
        repo = ApexRepo(RetroFitModule.provideRetrofitInstance())
        viewModel = BattleRoyalViewModel(repo)
    }

}