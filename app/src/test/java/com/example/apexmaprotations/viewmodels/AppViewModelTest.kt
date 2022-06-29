package com.example.apexmaprotations.viewmodels

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

class AppViewModelTest {
    private lateinit var appViewModel: AppViewModel

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
    fun showMenu_changesShowMenuValueToTrue() {
        appViewModel.showMenu()
        Truth.assertThat(appViewModel.showMenu.value).isEqualTo(true)
    }

    @Test
    fun hideMenu_changesShowMenuValueToFalse() {
        //  Make sure initial value is true
        appViewModel.showMenu()
        //  Set value to false
        appViewModel.hideMenu()
        Truth.assertThat(appViewModel.showMenu.value).isEqualTo(false)
    }
}