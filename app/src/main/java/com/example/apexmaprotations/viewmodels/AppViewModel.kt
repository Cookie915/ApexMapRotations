package com.example.apexmaprotations.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apexmaprotations.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {
    private var mShowSplash = MutableStateFlow(true)
    val showSplash: StateFlow<Boolean>
        get() = mShowSplash

    private var _backgroundImage = MutableStateFlow<Int?>(null)
    val backgroundImage
        get() = _backgroundImage

    private var _errStatus: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val errStatus: StateFlow<Boolean>
        get() = _errStatus

    private var _errImage = MutableStateFlow<Int?>(null)
    val errImage
        get() = _errImage

    private var _errMessage = MutableStateFlow<String?>(null)
    val errMessage
        get() = _errMessage


    fun hideSplash() {
        mShowSplash.value = false
    }

    private fun setBackgroundImage() {
        val image = getRandomBgImage()
        _backgroundImage.value = image
    }

    private fun getRandomBgImage(): Int {
        val bgImages = listOf(
            R.drawable.bg_ash,
            R.drawable.bg_bloodhound,
            R.drawable.bg_wraith,
            R.drawable.bg_octane,
            R.drawable.bg_valk,
            R.drawable.bg_pathy
        )
        val rand = Random()
        return bgImages[rand.nextInt(bgImages.size)]
    }

    private fun getRandomErrorImage(): Int {
        val errImages = listOf(
            R.drawable.er_bang,
            R.drawable.er_bloodhound,
            R.drawable.er_caustic,
            R.drawable.er_gibby,
            R.drawable.er_mirage,
            R.drawable.er_pathy,
            R.drawable.er_wraith
        )
        val rand = Random()
        return errImages[rand.nextInt(errImages.size)]
    }

    fun showError(message: String) {
        viewModelScope.launch {
            _errMessage.emit(message)
            _errImage.emit(getRandomErrorImage())
            _errStatus.emit(true)
        }
    }

    fun resetErrorState() {
        viewModelScope.launch {
            _errImage.emit(null)
            _errMessage.emit(null)
            _errStatus.emit(false)
        }
    }

    init {
        setBackgroundImage()
    }
}