package com.example.apexmaprotations.activities

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.MediumTest
import androidx.viewpager2.widget.ViewPager2
import com.example.apexmaprotations.R
import com.example.apexmaprotations.data.models.NetworkResult
import com.example.apexmaprotations.di.SingletonModule
import com.example.apexmaprotations.repo.ApexRepoImpl
import com.example.apexmaprotations.repo.FakeApexRepo
import com.example.apexmaprotations.viewmodels.ApexViewModel
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@MediumTest
@UninstallModules(SingletonModule::class)
@HiltAndroidTest
class MainActivityTest {
    @get:Rule(order = 0)
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var apexRepo: ApexRepoImpl

    @Before
    fun setup() {
        hiltAndroidRule.inject()
    }

    @After
    fun cleanup() {
        (apexRepo as FakeApexRepo).setShouldReturnNetworkError(false)
    }

    @Test
    fun swipeLeftFromBr_showArenasFragment() = runTest {
        val scene = activityScenarioRule.scenario
        var pager: ViewPager2? = null
        scene.onActivity {
            pager = it.findViewById(R.id.viewPager)
            it.apexViewModel.setFakeTimers()
        }
        onView(isRoot()).perform(swipeLeft())
        assertThat(pager).isNotNull()
        advanceUntilIdle()
        assertThat(pager?.currentItem).isEqualTo(1)
    }

    @Test
    fun swipeRightFromBr_doesNothing() = runTest {
        val scene = activityScenarioRule.scenario
        var pager: ViewPager2? = null
        scene.onActivity {
            pager = it.findViewById(R.id.viewPager)
            it.apexViewModel.setFakeTimers()
        }
        onView(isRoot()).perform(swipeRight())
        assertThat(pager?.currentItem).isEqualTo(0)
    }

    @Test
    fun swipeRightFromArenas_showBrFragment() = runTest {
        val scene = activityScenarioRule.scenario
        var pager: ViewPager2? = null
        scene.onActivity {
            pager = it.findViewById(R.id.viewPager)
            it.apexViewModel.setFakeTimers()
        }
        pager?.currentItem = 1
        onView(isRoot()).perform(swipeRight())
        advanceUntilIdle()
        assertThat(pager?.currentItem).isEqualTo(0)
    }

    @Test
    fun pressBackFromArenas_showsBrFragment() = runTest {
        val scene = activityScenarioRule.scenario
        var pager: ViewPager2? = null
        scene.onActivity {
            pager = it.findViewById(R.id.viewPager)
            it.apexViewModel.setFakeTimers()
        }
        pager?.currentItem = 1
        onView(isRoot()).perform(pressBack())
        advanceUntilIdle()
        assertThat(pager?.currentItem).isEqualTo(0)
    }

    @Test
    fun swipeLeftFromArenas_doesNothing() = runTest {
        val scene = activityScenarioRule.scenario
        var pager: ViewPager2? = null
        scene.onActivity {
            pager = it.findViewById(R.id.viewPager)
            it.apexViewModel.setFakeTimers()
        }
        pager?.currentItem = 1
        onView(isRoot()).perform(swipeLeft())
        assertThat(pager?.currentItem).isEqualTo(1)
    }

    @Test
    fun swipeDown_refreshesMapData() = runTest {
        //  Make repo return an error
        (apexRepo as FakeApexRepo).setShouldReturnNetworkError(true)
        val scene = activityScenarioRule.scenario
        var apexViewModel: ApexViewModel? = null
        scene.onActivity {
            it.apexViewModel.setFakeTimers()
            apexViewModel = it.apexViewModel
        }
        //  Make sure initial response is an error
        assertThat(apexViewModel?.mapDataBundle?.value).isInstanceOf(NetworkResult.Error::class.java)
        //  Make repo return successful response
        (apexRepo as FakeApexRepo).setShouldReturnNetworkError(false)
        //  Refresh the map data by swiping down
        onView(isRoot()).perform(swipeDown())
        //  Check new response is received and successful
        assertThat(apexViewModel?.mapDataBundle?.value).isInstanceOf(NetworkResult.Success::class.java)
    }


}
