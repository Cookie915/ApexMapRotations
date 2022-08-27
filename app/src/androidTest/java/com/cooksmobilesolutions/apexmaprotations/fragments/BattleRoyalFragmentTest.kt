package com.cooksmobilesolutions.apexmaprotations.fragments

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.cooksmobilesolutions.apexmaprotations.R
import com.cooksmobilesolutions.apexmaprotations.launchFragmentInHiltContainer
import com.cooksmobilesolutions.apexmaprotations.repo.ApexRepoImpl
import com.cooksmobilesolutions.apexmaprotations.repo.FakeApexRepo
import com.cooksmobilesolutions.apexmaprotations.testUtils.MainCoroutineRule
import com.cooksmobilesolutions.apexmaprotations.viewmodels.ApexViewModel
import com.cooksmobilesolutions.apexmaprotations.viewmodels.AppViewModel
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
@HiltAndroidTest
class BattleRoyalFragmentTest {
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

    @Test
    fun successfullyLoadingData_closesSplashScreen() = runTest {
        var testAppViewModel: AppViewModel? = null
        //  Map data initially called in onResume of fragment
        launchFragmentInHiltContainer<BattleRoyalFragment>() {
            testAppViewModel = this.appViewModel
            //  Necessary to use fake timers in apexviewmodel or main thread won't idle for espresso
            this.apexViewModel.setFakeTimers()
        }
        advanceUntilIdle()
        assertThat(testAppViewModel?.showSplash?.value).isFalse()
    }

    @Test
    fun clickingSetAlarmButton_setsIsAlarmSetStateTrue() = runTest {
        var testApexViewModel: ApexViewModel?
        var testAppViewModel: AppViewModel? = null
        launchFragmentInHiltContainer<BattleRoyalFragment>() {
            testApexViewModel = this.apexViewModel
            testAppViewModel = this.appViewModel
            testApexViewModel?.setFakeTimers()
        }
        advanceUntilIdle()
        testAppViewModel?.isAlarmSet?.test {
            //  Check initial value
            assertThat(awaitItem()).isFalse()
            //  Set alarm
            onView(withId(R.id.setAlarmBtn)).perform(click())
            //  Check alarm is set
            assertThat(awaitItem()).isTrue()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun clickingSetNotificationButton_setsIsNotificationSetStateTrue() = runTest {
        var testApexViewModel: ApexViewModel?
        var testAppViewModel: AppViewModel? = null
        launchFragmentInHiltContainer<BattleRoyalFragment>() {
            testApexViewModel = this.apexViewModel
            testAppViewModel = this.appViewModel
            testApexViewModel?.setFakeTimers()
        }
        advanceUntilIdle()
        testAppViewModel?.isNotificationSet?.test {
            //  Check initial value
            assertThat(awaitItem()).isFalse()
            //  Set alarm
            onView(withId(R.id.setNotificationButton)).perform(click())
            //  Check alarm is set
            assertThat(awaitItem()).isTrue()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun settingAlarm_disablesSetNotificationButton() = runTest {
        launchFragmentInHiltContainer<BattleRoyalFragment>() {
            apexViewModel.setFakeTimers()
        }
        onView(withId(R.id.setAlarmBtn)).perform(click())
        onView(withId(R.id.setNotificationButton)).check(matches(withAlpha(0.7f)))
        onView(withId(R.id.setNotificationButton)).check(matches(isNotClickable()))
        onView(withId(R.id.setNotificationText)).check(matches(withAlpha(0.7f)))
    }

    @Test
    fun settingNotification_disablesSetAlarmButton() = runTest {
        launchFragmentInHiltContainer<BattleRoyalFragment>() {
            apexViewModel.setFakeTimers()
        }
        onView(withId(R.id.setNotificationButton)).perform(click())
        onView(withId(R.id.setAlarmBtn)).check(matches(withAlpha(0.7f)))
        onView(withId(R.id.setAlarmBtn)).check(matches(isNotClickable()))
        onView(withId(R.id.setAlarmText)).check(matches(withAlpha(0.7f)))
    }

}