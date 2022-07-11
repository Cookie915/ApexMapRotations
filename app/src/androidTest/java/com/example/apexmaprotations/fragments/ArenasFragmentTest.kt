package com.example.apexmaprotations.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.example.apexmaprotations.MainCoroutineRule
import com.example.apexmaprotations.R
import com.example.apexmaprotations.launchFragmentInHiltContainer
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.viewmodels.ApexViewModel
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
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
    var coroutineRule = MainCoroutineRule()

    @Inject
    lateinit var testFragmentFactory: TestApexFragmentFactory

    @Before
    fun setup() {
        hiltAndroidRule.inject()
    }

    @Test
    fun test_TestsUseFakeRepo() = runTest {
        var testViewModel: ApexViewModel? = null
        testFragmentFactory.fakeRepo.setShouldReturnNetworkError(true)
        //  mapDataBundle initially fetched in fragments onResume()
        launchFragmentInHiltContainer<ArenasFragment>(fragmentFactory = testFragmentFactory) {
            testViewModel = this.apexViewModel
        }
        val job = launch {
            testViewModel?.mapDataBundle?.test {
                //  check initial
                assertThat(awaitItem()).isInstanceOf(NetworkResult.Loading::class.java)
                //  check first emission
                assertThat(awaitItem()).isInstanceOf(NetworkResult.Error::class.java)
                cancelAndIgnoreRemainingEvents()
            }
        }
        job.join()
        job.cancel()
    }

    @Test
    fun swipeRight_popsBackStack() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<ArenasFragment>(fragmentFactory = testFragmentFactory) {
            viewLifecycleOwnerLiveData.observeForever { lifecycleOwner ->
                if (lifecycleOwner != null) {
                    navController.setGraph(R.navigation.nav_graph)
                    Navigation.setViewNavController(requireView(), navController)
                }
            }
        }
        onView(isRoot()).perform(swipeRight())
        verify(navController).popBackStack()
    }

    @Test
    fun swipeLeft_doesNotPopBackStack() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<ArenasFragment>(fragmentFactory = testFragmentFactory) {
            viewLifecycleOwnerLiveData.observeForever { lifecycleOwner ->
                if (lifecycleOwner != null) {
                    navController.setGraph(R.navigation.nav_graph)
                    Navigation.setViewNavController(requireView(), navController)
                }
            }
        }
        verify(navController, never()).popBackStack()
    }
}