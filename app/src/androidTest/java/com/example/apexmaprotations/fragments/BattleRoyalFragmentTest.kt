package com.example.apexmaprotations.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.example.apexmaprotations.MainCoroutineRule
import com.example.apexmaprotations.launchFragmentInHiltContainer
import com.example.apexmaprotations.viewmodels.AppViewModel
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@MediumTest
@HiltAndroidTest
class BattleRoyalFragmentTest {
    @get:Rule(order = 0)
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    lateinit var fragmentFactory: TestApexFragmentFactory

    @Before
    fun setup() {
        hiltAndroidRule.inject()
    }

    @Test
    fun successfullyLoadingData_closesSplashScreen() = runTest {
        Dispatchers.resetMain()
        var testBattleRoyalViewModel: BattleRoyalViewModel? = null
        var testAppViewModel: AppViewModel? = null
        launchFragmentInHiltContainer<BattleRoyalFragment>(fragmentFactory = fragmentFactory) {
            testBattleRoyalViewModel = this.battleRoyalViewModel
            testAppViewModel = this.appViewModel
        }
        testBattleRoyalViewModel?.refreshMapData()
        val job = launch {
            testAppViewModel?.showSplash?.test {
                assertThat(awaitItem()).isEqualTo(true)
                assertThat(awaitItem()).isEqualTo(false)
            }
        }
        job.join()
        job.cancel()
    }

}