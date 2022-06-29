package com.example.apexmaprotations.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.example.apexmaprotations.MainCoroutineRule
import com.example.apexmaprotations.R
import com.example.apexmaprotations.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@HiltAndroidTest
@MediumTest
class ArenasFragmentTest {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    //  Switches main dispatcher when running tests
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        hiltAndroidRule.inject()
    }

    @Test
    fun swipeRight_popsBackStack() {
        val navController = mock(NavController::class.java)

        launchFragmentInHiltContainer<ArenasFragment> {}

        onView(withId(R.id.arenasFragment)).perform(swipeRight())

        verify(navController).popBackStack()

    }
}