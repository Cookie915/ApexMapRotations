package com.example.apexmaprotations.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apexmaprotations.data.models.NetworkResult
import com.example.apexmaprotations.data.retrofit.MapDataBundle
import com.example.apexmaprotations.repo.ApexRepoImpl
import com.example.apexmaprotations.util.CustomCountdownTimer
import com.example.apexmaprotations.util.NEXT_MAP_KEY
import com.example.apexmaprotations.util.formatTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "ApexViewModel"

@HiltViewModel
class ApexViewModel @Inject constructor(
    private val apexRepo: ApexRepoImpl,
    val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _mapDataBundle: MutableStateFlow<NetworkResult<MapDataBundle>?> =
        MutableStateFlow(null)
    val mapDataBundle: StateFlow<NetworkResult<MapDataBundle>?> = _mapDataBundle

    private var provideFakeTimers: Boolean = false

    //  Only use this method in tests
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setFakeTimers() {
        provideFakeTimers = true
        refreshMapData()
    }

    private var _timeRemainingBr = MutableStateFlow<Long>(0)
    val timeRemainingBr: StateFlow<List<Any>>
        get() = _timeRemainingBr.transform {
            val hours = it / (1000 * 60 * 60) % 60
            val minutes = it / (1000 * 60) % 60
            val seconds = it / (1000) % 60
            val decimal = it / 100 % 10
            val times = formatTime(minutes, seconds)
            val list = listOf<Any>(hours, times.first(), times.last(), decimal)
            emit(list)
        }.stateIn(viewModelScope, SharingStarted.Lazily, listOf(0, "0.0", "0.0", 0))

    //  Ranked Arenas Timer
    private var _timeRemainingArenas = MutableStateFlow<Long>(0)
    val timeRemainingArenas: StateFlow<List<Any>>
        get() = _timeRemainingArenas.transform {
            val minutes = it / (1000 * 60) % 60
            val seconds = it / (1000) % 60
            val decimal = it / 100 % 10
            val times = formatTime(minutes, seconds)
            val list = listOf<Any>(times.first(), times.last(), decimal)
            emit(list)
        }.stateIn(viewModelScope, SharingStarted.Lazily, listOf("0.0", "0.0", 0))

    //  Unranked Arenas Timer
    private var _timeRemainingArenasRanked = MutableStateFlow<Long>(0)
    val timeRemainingArenasRanked: StateFlow<List<Any>>
        get() = _timeRemainingArenasRanked.transform {
            val minutes = it / (1000 * 60) % 60
            val seconds = it / (1000) % 60
            val decimal = it / 100 % 10
            val times = formatTime(minutes, seconds)
            val list = listOf<Any>(times.first(), times.last(), decimal)
            emit(list)
        }.stateIn(viewModelScope, SharingStarted.Lazily, listOf("0.0", "0.0", 0))

    var timerBr: CustomCountdownTimer? = null
    var rankedTimerArenas: CustomCountdownTimer? = null
    var unrankedTimerArenas: CustomCountdownTimer? = null

    fun refreshMapData() {
        Log.i("test2", "refreshmapdata")
        viewModelScope.launch {
            apexRepo._mapData.collect {
                when (it) {
                    is NetworkResult.Loading -> {
                        _mapDataBundle.value = NetworkResult.Loading()
                    }
                    is NetworkResult.Error -> {
                        _mapDataBundle.value = NetworkResult.Error("${it.message}", it.data)
                    }
                    is NetworkResult.Success -> {
                        initializeTimers(it.data!!)
                        _mapDataBundle.value = NetworkResult.Success(it.data)
                    }
                }
            }
        }
    }

    fun setNextMapPreferences(nextMap: String) {
        viewModelScope.launch {
            with(sharedPreferences.edit()) {
                putString(NEXT_MAP_KEY, nextMap)
                commit()
            }
        }
    }

    private fun initializeTimers(mapData: MapDataBundle) {
        cancelTimers()
        if (provideFakeTimers) {
            //  Provide fake timers for testing
            cancelTimers()
        } else {
            //  Provide real timers
            val remainingTimeUnranked = mapData.arenas.current.remainingSecs * 1000L
            val remainingTimeRanked = mapData.arenasRanked.current.remainingSecs * 1000L
            val remainingTimeBr = mapData.battleRoyale.current.remainingSecs * 1000L
            timerBr = object : CustomCountdownTimer(remainingTimeBr, 1) {
                override fun onTick(millisUntilFinished: Long) {
                    viewModelScope.launch {
                        _timeRemainingBr.value = millisUntilFinished
                    }
                }

                override suspend fun onFinish() {
                    viewModelScope.launch {
                        delay(1000L)
                        cancelTimers()
                        refreshMapData()
                    }
                }
            }
            rankedTimerArenas = object : CustomCountdownTimer(remainingTimeRanked, 1) {
                override fun onTick(millisUntilFinished: Long) {
                    viewModelScope.launch {
                        _timeRemainingArenasRanked.value = millisUntilFinished
                    }
                }

                override suspend fun onFinish() {
                    viewModelScope.launch {
                        delay(1500L)
                        cancelTimers()
                        refreshMapData()
                    }
                }
            }
            unrankedTimerArenas = object : CustomCountdownTimer(remainingTimeUnranked, 1) {
                override fun onTick(millisUntilFinished: Long) {
                    viewModelScope.launch {
                        _timeRemainingArenas.emit(millisUntilFinished)
                    }
                }

                override suspend fun onFinish() {
                    viewModelScope.launch {
                        delay(2000L)
                        cancelTimers()
                        refreshMapData()
                    }
                }
            }
            timerBr?.start()
            rankedTimerArenas?.start()
            unrankedTimerArenas?.start()
        }
    }

    fun checkTimers(calendar: Calendar) {
        val currentTimeMillis = calendar.timeInMillis
        if (timerBr != null && rankedTimerArenas != null && unrankedTimerArenas != null) {
            if (
                (timerBr?.mStopTimeInFutureMillis ?: 0) < currentTimeMillis ||
                ((rankedTimerArenas?.mStopTimeInFutureMillis ?: 0) < currentTimeMillis) ||
                (unrankedTimerArenas?.mStopTimeInFutureMillis ?: 0) < currentTimeMillis
            ) {
                refreshMapData()
            }
        }
    }

    private fun cancelTimers() {
        timerBr?.cancel(); timerBr = null
        rankedTimerArenas?.cancel(); rankedTimerArenas = null
        unrankedTimerArenas?.cancel(); unrankedTimerArenas = null
    }

    public override fun onCleared() {
        super.onCleared()
        cancelTimers()
        Log.i(TAG, "vm cleared")
    }

}