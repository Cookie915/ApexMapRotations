package com.example.apexmaprotations.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apexmaprotations.models.Resource
import com.example.apexmaprotations.models.asResource
import com.example.apexmaprotations.models.retrofit.MapDataBundle
import com.example.apexmaprotations.models.retrofit.RetroFitInstance
import com.example.apexmaprotations.models.toStateFlow
import com.example.apexmaprotations.repo.ApexRepo
import com.example.apexmaprotations.util.formatTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArenasViewModel @Inject constructor(
    private val apexRepo: ApexRepo
) : ViewModel() {
    private var mMapDataBundle: Flow<Resource<MapDataBundle?>> =
        apexRepo.getMapDataData().asResource()
    val mapDataBundle: StateFlow<Resource<MapDataBundle?>>
        get() = mMapDataBundle.toStateFlow(viewModelScope, Resource.Loading)

    private var mTimeRemaining = MutableStateFlow<Long>(0)
    val timeRemaining: StateFlow<List<Any>>
        get() = mTimeRemaining.transform {
            val hours = it / (1000 * 60 * 60) % 60
            val minutes = it / (1000 * 60) % 60
            val seconds = it / (1000) % 60
            val decimal = it / 100 % 10
            val times = formatTime(minutes, seconds)
            val list = listOf<Any>(hours, times.first(), times.last(), decimal)
            emit(list)
        }.toStateFlow(viewModelScope, listOf(0, "0.0", "0.0", 0))
    val timeRemainingLong: StateFlow<Long>
        get() = mTimeRemaining

    private var mTimeRemainingRanked = MutableStateFlow<Long>(0)
    val timeRemainingRanked: StateFlow<List<Any>>
        get() = mTimeRemainingRanked.transform {
            val hours = it / (1000 * 60 * 60) % 60
            val minutes = it / (1000 * 60) % 60
            val seconds = it / (1000) % 60
            val decimal = it / 100 % 10
            val times = formatTime(minutes, seconds)
            val list = listOf<Any>(hours, times.first(), times.last(), decimal)
            emit(list)
        }.toStateFlow(viewModelScope, listOf(0, "0.0", "0.0", 0))
    val timeRemainingRankedLong: StateFlow<Long>
        get() = mTimeRemainingRanked

    fun initializeTimers(mapDataBundle: MapDataBundle) {
        val remainingTimer = mapDataBundle.arenas.current.remainingSecs
        val remainingTimerRanked = mapDataBundle.arenasRanked.current.remainingSecs
        CoroutineScope(Dispatchers.Main).launch {
            val timer = object : CountDownTimer(remainingTimer * 1000L, 1) {
                override fun onTick(millisUntilFinished: Long) {
                    mTimeRemaining.value = millisUntilFinished
                }

                override fun onFinish() {
                    viewModelScope.launch {
                        RetroFitInstance.apexApi.getMaps()
                        mMapDataBundle.collect { dataBundle ->
                            if (dataBundle is Resource.Success) {
                                initializeTimers(mapDataBundle)
                            }
                            mTimeRemaining.value = 0L
                        }
                    }
                }
            }
            val timerRanked = object : CountDownTimer(remainingTimerRanked * 1000L, 1) {
                override fun onTick(millisUntilFinished: Long) {
                    mTimeRemainingRanked.value = millisUntilFinished
                }

                override fun onFinish() {
                    viewModelScope.launch {
                        RetroFitInstance.apexApi.getMaps()
                        mMapDataBundle.collect { dataBundle ->
                            if (dataBundle is Resource.Success) {
                                initializeTimers(mapDataBundle)
                            }
                            mTimeRemainingRanked.value = 0L
                        }
                    }
                }
            }
            if (mTimeRemaining.value == 0L) {
                timer.start()
                timerRanked.start()
            }
        }
    }

}