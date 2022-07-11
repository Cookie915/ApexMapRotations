package com.example.apexmaprotations.activities

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.example.apexmaprotations.MainCoroutineRule
import com.example.apexmaprotations.R
import com.example.apexmaprotations.fragments.TestApexFragmentFactory
import com.example.apexmaprotations.viewmodels.AppViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@MediumTest
@HiltAndroidTest
class MainActivityTest {
    @get:Rule(order = 0)
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    //  Switches main dispatcher when running tests
    @get:Rule(order = 2)
    var coroutineRule = MainCoroutineRule()

    @Inject
    lateinit var testFragmentFactory: TestApexFragmentFactory

    @Before
    fun setup() {
        hiltAndroidRule.inject()
    }

    @Test
    fun t() = runTest {
        var testAppViewModel: AppViewModel? = null
        val activity: ActivityScenario<MainActivity> =
            ActivityScenario.launch(MainActivity::class.java)
        activity.onActivity {
            testAppViewModel = it.appViewModel
        }
        testAppViewModel?.showError("Error")
        val job = launch {
            testAppViewModel?.errStatus?.test {
                awaitItem()
                onView(withId(R.id.errImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            }
        }
        job.join()
    }

}
