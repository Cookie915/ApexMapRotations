package com.example.apexmaprotations.viewmodels

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.testIn
import com.example.apexmaprotations.MainCoroutineRule
import com.example.apexmaprotations.testUtils.TestDispatchers
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    @get:Rule(order = 1)
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule(order = 2)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var appViewModel: AppViewModel

    @Before
    fun setup() {
        val sharedPreferences = mock(SharedPreferences::class.java)
        appViewModel = AppViewModel(TestDispatchers(), sharedPreferences)
    }


    @Test
    fun hideSplash_changesShowSplashValue() = runTest {
        val showSplashTurbine = appViewModel.showSplash.testIn(this)
        //  check initial value
        assertThat(showSplashTurbine.awaitItem()).isEqualTo(true)
        //  hide the splash
        appViewModel.hideSplash()
        //  make sure state is changed
        assertThat(showSplashTurbine.awaitItem()).isEqualTo(false)
        // cleanup turbine
        showSplashTurbine.cancelAndConsumeRemainingEvents()
    }

    @Test
    fun showError_setsViewModelState() = runTest {
        //  Flows under test
        val errorStatusTurbine = appViewModel.errStatus.testIn(this)
        val errorMessageTurbine = appViewModel.errMessage.testIn(this)
        val errorImageTurbine = appViewModel.errImage.testIn(this)
        //  Test initial values
        assertThat(errorStatusTurbine.awaitItem()).isEqualTo(false)
        assertThat(errorMessageTurbine.awaitItem()).isNull()
        assertThat(errorImageTurbine.awaitItem()).isNull()
        //  Show an Error
        appViewModel.showError("Error")
        //  Test new values
        assertThat(errorStatusTurbine.awaitItem()).isEqualTo(true)
        assertThat(errorMessageTurbine.awaitItem()).isEqualTo("Error")
        assertThat(errorImageTurbine.awaitItem()).isNotNull()
        //  cleanup turbines
        errorImageTurbine.cancelAndConsumeRemainingEvents()
        errorMessageTurbine.cancelAndConsumeRemainingEvents()
        errorStatusTurbine.cancelAndConsumeRemainingEvents()
    }

    @Test
    fun resetError_resetsErrorImageMessageAndState() = runTest {
        val errorStatusTurbine = appViewModel.errStatus.testIn(this)
        val errorMessageTurbine = appViewModel.errMessage.testIn(this)
        val errorImageTurbine = appViewModel.errImage.testIn(this)
        //  skip initial values
        errorStatusTurbine.skipItems(1)
        errorMessageTurbine.skipItems(1)
        errorImageTurbine.skipItems(1)
        //  Show an error and update state
        appViewModel.showError("Error")
        //  Make sure error state is set
        assertThat(errorStatusTurbine.awaitItem()).isEqualTo(true)
        assertThat(errorMessageTurbine.awaitItem()).isEqualTo("Error")
        assertThat(errorImageTurbine.awaitItem()).isNotNull()
        //  Reset error state
        appViewModel.resetErrorState()
        // verify error state is reset
        assertThat(errorStatusTurbine.awaitItem()).isEqualTo(false)
        assertThat(errorMessageTurbine.awaitItem()).isEqualTo(null)
        assertThat(errorImageTurbine.awaitItem()).isNull()
        //  clean up turbines
        errorStatusTurbine.cancelAndConsumeRemainingEvents()
        errorMessageTurbine.cancelAndConsumeRemainingEvents()
        errorImageTurbine.cancelAndConsumeRemainingEvents()
    }

}