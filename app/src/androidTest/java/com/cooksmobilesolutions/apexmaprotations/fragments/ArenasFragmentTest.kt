package com.cooksmobilesolutions.apexmaprotations.fragments

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.cooksmobilesolutions.apexmaprotations.data.models.NetworkResult
import com.cooksmobilesolutions.apexmaprotations.launchFragmentInHiltContainer
import com.cooksmobilesolutions.apexmaprotations.repo.ApexRepoImpl
import com.cooksmobilesolutions.apexmaprotations.repo.FakeApexRepo
import com.cooksmobilesolutions.apexmaprotations.testUtils.MainCoroutineRule
import com.cooksmobilesolutions.apexmaprotations.viewmodels.ApexViewModel
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


@ExperimentalCoroutinesApi
@MediumTest
@HiltAndroidTest
class ArenasFragmentTest {
    @get:Rule(order = 0)
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var repo: ApexRepoImpl

    @Before
    fun setup() {
        hiltAndroidRule.inject()
    }

    @After
    fun teardown() {
        (repo as FakeApexRepo).setShouldReturnNetworkError(false)
        with(sharedPreferences.edit()) {
            clear()
            commit()
        }
    }

    //  This test uses the StandardTestDispatcher() because I wanted to play
    //  around and get a feel for the different ways
    //  test handle dispatching and starting coroutines
    @Test
    fun test_TestsUseFakeRepo() = runTest {
        var testViewModel: ApexViewModel? = null
        (repo as FakeApexRepo).setShouldReturnNetworkError(true)
        //  Starts listening for emissions
        val job = launch {
            testViewModel?.mapDataBundle?.test {
                //  check first emission
                assertThat(awaitItem()).isNull()
                //  check loading
                assertThat(awaitItem()).isInstanceOf(NetworkResult.Loading::class.java)
                //  check error
                assertThat(awaitItem()).isInstanceOf(NetworkResult.Error::class.java)
                cancelAndIgnoreRemainingEvents()
            }
        }
        job.join()
        //  mapDataBundle initially fetched in viewmodel init{}
        launchFragmentInHiltContainer<ArenasFragment> {
            testViewModel = this.apexViewModel
        }
        job.cancel()
    }

    @Test
    fun test_TestsUseFakeRepoUnconfined() = runTest {
        var testViewModel: ApexViewModel? = null
        (repo as FakeApexRepo).setShouldReturnNetworkError(true)
        //  mapDataBundle initially fetched in viewmodel init{}
        launchFragmentInHiltContainer<ArenasFragment> {
            testViewModel = this.apexViewModel
        }
        advanceUntilIdle()
        assertThat(testViewModel?.mapDataBundle?.value).isInstanceOf(NetworkResult.Error::class.java)
    }

}