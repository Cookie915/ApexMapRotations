package com.example.apexmaprotations.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.apexmaprotations.MainCoroutineRule
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.repo.FakeApexRepo
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ArenasViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var arenasViewModel: ArenasViewModel

    lateinit var repo: FakeApexRepo

    @Before
    fun setup() {
        repo = FakeApexRepo()
        arenasViewModel = ArenasViewModel(repo)
    }

    @Test
    fun mapDataIsNetworkError_whenRepoReturnError() = runTest {
        repo.setShouldReturnNetworkError(true)
        val job = launch {
            arenasViewModel.mapDataBundle.test {
                assertThat(awaitItem()).isInstanceOf(NetworkResult.Loading::class.java)
                assertThat(awaitItem()).isInstanceOf(NetworkResult.Error::class.java)
                cancelAndIgnoreRemainingEvents()
            }
        }
        arenasViewModel.refreshMapData()
        job.join()
        job.cancel()
    }

    @Test
    fun mapDataIsNetworkSuccess_whenRepoReturnsSuccessfully() = runTest {
        repo.setShouldReturnNetworkError(false)
        val job = launch {
            arenasViewModel.mapDataBundle.test {
                //  1st emission
                assertThat(awaitItem()).isInstanceOf(NetworkResult.Loading::class.java)
                //  2nd emission
                assertThat(awaitItem()).isInstanceOf(NetworkResult.Success::class.java)
                cancelAndIgnoreRemainingEvents()
            }
        }
        arenasViewModel.refreshMapData()
        job.join()
        job.cancel()

    }

}