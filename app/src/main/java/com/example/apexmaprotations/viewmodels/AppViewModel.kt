package com.example.apexmaprotations.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel : ViewModel() {
    private var mShowMenu = MutableStateFlow(false)
    val showMenu: StateFlow<Boolean>
        get() = mShowMenu

    private var mShowSplash = MutableStateFlow(true)
    val showSplash: StateFlow<Boolean>
        get() = mShowSplash

    fun hideSplash() {
        mShowSplash.value = false
    }

    fun showMenu() {
        mShowMenu.value = true
    }

    fun hideMenu() {
        mShowMenu.value = false
    }
}