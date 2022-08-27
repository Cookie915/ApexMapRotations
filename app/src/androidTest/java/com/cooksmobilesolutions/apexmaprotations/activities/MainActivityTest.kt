package com.cooksmobilesolutions.apexmaprotations.activities

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.filters.MediumTest
import androidx.viewpager2.widget.ViewPager2
import com.cooksmobilesolutions.apexmaprotations.R
import com.cooksmobilesolutions.apexmaprotations.data.models.NetworkResult
import com.cooksmobilesolutions.apexmaprotations.di.SingletonModule
import com.cooksmobilesolutions.apexmaprotations.repo.ApexRepoImpl
import com.cooksmobilesolutions.apexmaprotations.repo.FakeApexRepo
import com.cooksmobilesolutions.apexmaprotations.testUtils.MainCoroutineRule
import com.cooksmobilesolutions.apexmaprotations.util.SHOW_INTO_SCREEN_KEY
import com.cooksmobilesolutions.apexmaprotations.viewmodels.ApexViewModel
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
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    lateinit var apexRepo: ApexRepoImpl

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var preferenceEditor: SharedPreferences.Editor

    private var activityScenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setup() {
        hiltAndroidRule.inject()
        preferenceEditor = sharedPreferences.edit()
    }

    @After
    fun cleanup() {
        (apexRepo as FakeApexRepo).setShouldReturnNetworkError(false)
        preferenceEditor.clear().apply()
        activityScenario?.close()
        activityScenario = null
    }

    @Test
    fun swipeLeftFromBr_showArenasFragment() = runTest {
        preferenceEditor.putBoolean(SHOW_INTO_SCREEN_KEY, false).commit()
        var pager: ViewPager2? = null
        activityScenario = ActivityScenario.launch(MainActivity::class.java).onActivity {
            it.apexViewModel.setFakeTimers()
            pager = it.findViewById(R.id.viewPager)
        }
        advanceUntilIdle()
        onView(isRoot()).perform(swipeLeft())
        assertThat(pager).isNotNull()
        assertThat(pager?.currentItem).isEqualTo(1)
    }

    @Test
    fun swipeRightFromBr_doesNothing() = runTest {
        preferenceEditor.putBoolean(SHOW_INTO_SCREEN_KEY, false).commit()
        var pager: ViewPager2? = null
        activityScenario = ActivityScenario.launch(MainActivity::class.java).onActivity {
            it.apexViewModel.setFakeTimers()
            pager = it.findViewById(R.id.viewPager)
        }
        advanceUntilIdle()
        onView(isRoot()).perform(swipeRight())
        assertThat(pager?.currentItem).isEqualTo(0)
    }

    @Test
    fun swipeRightFromArenas_showBrFragment() = runTest {
        preferenceEditor.putBoolean(SHOW_INTO_SCREEN_KEY, false).commit()
        var pager: ViewPager2? = null
        activityScenario = ActivityScenario.launch(MainActivity::class.java).onActivity {
            it.apexViewModel.setFakeTimers()
            pager = it.findViewById(R.id.viewPager)
        }
        pager?.currentItem = 1
        onView(isRoot()).perform(swipeRight())
        advanceUntilIdle()
        assertThat(pager?.currentItem).isEqualTo(0)
    }

    @Test
    fun pressBackFromArenas_showsBrFragment() = runTest {
        preferenceEditor.putBoolean(SHOW_INTO_SCREEN_KEY, false).commit()
        var pager: ViewPager2? = null
        activityScenario = ActivityScenario.launch(MainActivity::class.java).onActivity {
            it.apexViewModel.setFakeTimers()
            pager = it.findViewById(R.id.viewPager)
        }
        pager?.currentItem = 1
        onView(isRoot()).perform(pressBack())
        advanceUntilIdle()
        assertThat(pager?.currentItem).isEqualTo(0)
    }

    @Test
    fun swipeLeftFromArenas_doesNothing() = runTest {
        preferenceEditor.putBoolean(SHOW_INTO_SCREEN_KEY, false).commit()
        var pager: ViewPager2? = null
        activityScenario = ActivityScenario.launch(MainActivity::class.java).onActivity {
            it.apexViewModel.setFakeTimers()
            pager = it.findViewById(R.id.viewPager)
        }
        pager?.currentItem = 1
        onView(isRoot()).perform(swipeLeft())
        assertThat(pager?.currentItem).isEqualTo(1)
    }

    @Test
    fun swipeDown_refreshesMapData() = runTest {
        //  Make repo return an error
        (apexRepo as FakeApexRepo).setShouldReturnNetworkError(true)
        //  Hide intro screen
        preferenceEditor.putBoolean(SHOW_INTO_SCREEN_KEY, false).commit()
        var apexViewModel: ApexViewModel? = null
        activityScenario = ActivityScenario.launch(MainActivity::class.java).onActivity {
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