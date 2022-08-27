package com.cooksmobilesolutions.apexmaprotations.activities

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.cooksmobilesolutions.apexmaprotations.R
import com.cooksmobilesolutions.apexmaprotations.di.SingletonModule
import com.cooksmobilesolutions.apexmaprotations.repo.ApexRepoImpl
import com.cooksmobilesolutions.apexmaprotations.repo.FakeApexRepo
import com.cooksmobilesolutions.apexmaprotations.testUtils.ViewPager2Actions
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
class IntroActivityTests {
    @get:Rule(order = 0)
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var apexRepo: ApexRepoImpl

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var preferenceEditor: SharedPreferences.Editor

    private var scene: ActivityScenario<MainActivity>? = null


    @Before
    fun setup() {
        hiltAndroidRule.inject()
        preferenceEditor = sharedPreferences.edit()
    }

    @After
    fun cleanup() {
        (apexRepo as FakeApexRepo).setShouldReturnNetworkError(false)
        preferenceEditor.clear().apply()
        scene?.close()
        scene = null
    }

    @Test
    fun introScreenIsDisplayed_whenLaunchingForFirstTime() = runTest {
        scene = ActivityScenario.launch(MainActivity::class.java).onActivity {
            it.apexViewModel.setFakeTimers()
        }
        Espresso.onView(ViewMatchers.withId(R.id.IntroScreenTextView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun finishIntroButton_closesIntroScreen() = runTest {
        scene = ActivityScenario.launch(MainActivity::class.java).onActivity {
            it.apexViewModel.setFakeTimers()
        }
        //  Make sure intro screen is displayed
        Espresso.onView(ViewMatchers.withId(R.id.IntroScreenTextView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        //  Scroll to last page
        Espresso.onView(ViewMatchers.withId(R.id.IntroScreenViewPager))
            .perform(ViewPager2Actions.scrollToLast())
        //  Click
        Espresso.onView(ViewMatchers.withId(R.id.finishIntroButton)).perform(ViewActions.click())
        //  Make sure intro screen is closed
        Espresso.onView(ViewMatchers.withId(R.id.IntroScreenTextView))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun introScreenNotShown_whenLaunchingSecondTime() = runTest {
        scene = ActivityScenario.launch(MainActivity::class.java).onActivity {
            it.apexViewModel.setFakeTimers()
        }
        //  Check Intro Shown on first launch
        Espresso.onView(ViewMatchers.withId(R.id.IntroScreenTextView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        //  Scroll to last page
        Espresso.onView(ViewMatchers.withId(R.id.IntroScreenViewPager))
            .perform(ViewPager2Actions.scrollToLast())
        //  Click finish intro
        Espresso.onView(ViewMatchers.withId(R.id.finishIntroButton)).perform(ViewActions.click())
        scene?.close()
        //  Relaunch activity
        scene = ActivityScenario.launch(MainActivity::class.java).onActivity {
            it.apexViewModel.setFakeTimers()
        }
        advanceUntilIdle()
        //  Check intro no longer shown
        Espresso.onView(ViewMatchers.withId(R.id.IntroScreenTextView))
            .check(ViewAssertions.doesNotExist())
    }
}