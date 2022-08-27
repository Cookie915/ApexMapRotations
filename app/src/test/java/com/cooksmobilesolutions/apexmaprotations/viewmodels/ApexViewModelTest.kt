package com.cooksmobilesolutions.apexmaprotations.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.testIn
import com.cooksmobilesolutions.apexmaprotations.MainCoroutineRule
import com.cooksmobilesolutions.apexmaprotations.data.models.NetworkResult
import com.cooksmobilesolutions.apexmaprotations.repo.FakeApexRepo
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*
import kotlin.math.min

@HiltAndroidTest
@ExperimentalCoroutinesApi
class ApexViewModelTest {
    @get:Rule(order = 1)
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule(order = 2)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var apexViewModel: ApexViewModel

    lateinit var repo: FakeApexRepo

    @Before
    fun setup() {
        val sharedPreferences = mock(SharedPreferences::class.java)
        repo = FakeApexRepo(mainCoroutineRule.dispatchers)
        apexViewModel = ApexViewModel(repo, sharedPreferences)
    }

    @Test
    fun mapDataIsNetworkError_whenRepoReturnsError() = runTest {
        apexViewModel.setFakeTimers()
        repo.setShouldReturnNetworkError(true)
        apexViewModel.refreshMapData()
        advanceUntilIdle()
        assertThat(apexViewModel.mapDataBundle.value).isInstanceOf(NetworkResult.Error::class.java)
    }

    @Test
    fun mapDataIsNetworkSuccess_whenRepoReturnsSuccessfully() = runTest() {
        apexViewModel.setFakeTimers()
        repo.setShouldReturnNetworkError(false)
        val mapDataBundleTurbine = apexViewModel.mapDataBundle.testIn(this)
        apexViewModel.refreshMapData()
        assertThat(mapDataBundleTurbine.awaitItem()).isInstanceOf(NetworkResult.Success::class.java)
        mapDataBundleTurbine.cancelAndConsumeRemainingEvents()
    }

    @Test
    fun refreshMapData_Succeeds() = runTest {
        apexViewModel.refreshMapData()
        advanceUntilIdle()
        assertThat(apexViewModel.mapDataBundle.value).isInstanceOf(NetworkResult.Success::class.java)
    }

    @Test
    fun mapDataRefreshed_whenTimerExpired() = runTest {
        //  Spy on viewmodel
        val spy = spy(apexViewModel)
        spy.setFakeTimers()
        advanceUntilIdle()
        //  set time AFTER when latest timer was supposed to finish
        val time =
            (apexViewModel.mapDataBundle.value?.data?.brEndTimeSecs?.times(1000)?.plus(10000L))
        //  Make calendar return time created above
        val calendarSpy = mock(Calendar::class.java)
        `when`(calendarSpy.timeInMillis).thenReturn(time)
        //  Check the timers, should call refresh map data since time is past timers scheduled finish time
        spy.checkTimers(calendarSpy)

        verify(spy, times(2)).refreshMapData()
    }

    @Test
    fun mapDataNotRefreshed_whenTimerNotExpired() = runTest {
        //  Spy on viewmodel
        val spy = spy(apexViewModel)
        spy.setFakeTimers()
        advanceUntilIdle()

        // Find lowest remaining time from mapdata
        val lesserTime = min(
            apexViewModel.mapDataBundle.value?.data?.arenasEndTimeSecs ?: 0,
            apexViewModel.mapDataBundle.value?.data?.rankedArenasEndTimeSecs ?: 0
        )
        //  set time BEFORE when lowest timer was supposed to finish
        val time = (lesserTime * 1000L) - 10000L

        //  Make calendar return time created above
        val calendarSpy = mock(Calendar::class.java)
        `when`(calendarSpy.timeInMillis).thenReturn(time)

        //  Check the timers, shouldn't refresh mapData
        spy.checkTimers(calendarSpy)

        verify(spy, atMostOnce()).refreshMapData()
    }

    @Test
    fun cancelTimers_setsAllTimersToNull() = runTest {
        apexViewModel.setFakeTimers()
        advanceUntilIdle()
        //  Make sure timers are initially set
        assertThat(apexViewModel.timerBr).isNotNull()
        //  Clear the viewmodel, calls cancelTimers()
        apexViewModel.onCleared()
        advanceUntilIdle()
        //  Make sure timers are all null
        assertThat(apexViewModel.timerBr).isNull()
        assertThat(apexViewModel.unrankedTimerArenas).isNull()
        assertThat(apexViewModel.rankedTimerArenas).isNull()
    }

}