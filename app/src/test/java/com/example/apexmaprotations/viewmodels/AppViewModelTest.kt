package com.example.apexmaprotations.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.apexmaprotations.MainCoroutineRule
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {
    private lateinit var appViewModel: AppViewModel

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        appViewModel = AppViewModel()
    }

    @Test
    fun hideSplash_changesShowSplashValue() {
        appViewModel.hideSplash()
        Truth.assertThat(appViewModel.showSplash.value).isEqualTo(false)
    }

    @Test
    fun resetErrorStateFun_properlyResetsErrorState() = runTest {
        val job = launch {
            appViewModel.errStatus.test {
                //  collect initial emission
                Truth.assertThat(awaitItem()).isEqualTo(false)
                //  collect error emission
                Truth.assertThat(awaitItem()).isEqualTo(true)
                //  make sure error status is reset
                Truth.assertThat(awaitItem()).isEqualTo(false)
                cancelAndIgnoreRemainingEvents()
            }
        }
        appViewModel.showError("Test Error")
        appViewModel.resetErrorState()
        job.join()
        job.cancel()
    }

    @Test
    fun appViewModelShowErrorFun_setsErrorImageAndMessage() = runTest {
        //  Check initial value is false
        Truth.assertThat(appViewModel.errStatus.value).isEqualTo(false)
        //  Show error
        appViewModel.showError("Test Error")
        val job = launch {
            //  Status error is true
            appViewModel.errStatus.test {
                Truth.assertThat(awaitItem()).isEqualTo(true)
            }
            //  errImage is set
            appViewModel.errImage.test {
                Truth.assertThat(awaitItem()).isNotNull()
            }
            //  errMsg is equal to string passed in showError()
            appViewModel.errMessage.test {
                Truth.assertThat(awaitItem()).isEqualTo("Test Error")
            }
        }
        job.join()
    }
}