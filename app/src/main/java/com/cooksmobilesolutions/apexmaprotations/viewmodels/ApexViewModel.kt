package com.cooksmobilesolutions.apexmaprotations.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cooksmobilesolutions.apexmaprotations.data.models.MapData
import com.cooksmobilesolutions.apexmaprotations.data.models.NetworkResult
import com.cooksmobilesolutions.apexmaprotations.repo.ApexRepoImpl
import com.cooksmobilesolutions.apexmaprotations.util.CustomCountDownTimer
import com.cooksmobilesolutions.apexmaprotations.util.NEXT_MAP_KEY
import com.cooksmobilesolutions.apexmaprotations.util.formatTime
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val _mapDataBundle: MutableStateFlow<NetworkResult<MapData>?> =
        MutableStateFlow(null)
    val mapDataBundle: StateFlow<NetworkResult<MapData>?> = _mapDataBundle

    var timerBr: CustomCountDownTimer? = null
    var rankedTimerArenas: CustomCountDownTimer? = null
    var unrankedTimerArenas: CustomCountDownTimer? = null

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
                apply()
            }
        }
    }

    private fun initializeTimers(mapData: MapData) {
        cancelTimers()
        //  Provide real timers
        val brTimeRemaining = ((mapData.brEndTimeSecs).times(1000))
        val arenasTimeRemaining = ((mapData.arenasEndTimeSecs).times(1000))
        val arenasRankedTimeRemaining = ((mapData.rankedArenasEndTimeSecs).times(1000))
        timerBr = object : CustomCountDownTimer(brTimeRemaining, 1) {
            override fun onTick(millisUntilFinished: Long) {
                viewModelScope.launch {
                    _timeRemainingBr.value = millisUntilFinished
                }
            }

            override fun onFinish() {
                viewModelScope.launch {
//                        refreshMapData()
                }
            }
        }
        rankedTimerArenas = object : CustomCountDownTimer(arenasRankedTimeRemaining, 1) {
            override fun onTick(millisUntilFinished: Long) {
                viewModelScope.launch {
                    _timeRemainingArenasRanked.value = millisUntilFinished
                }
            }

            override fun onFinish() {
                viewModelScope.launch {
//                        refreshMapData()
                }
            }
        }
        unrankedTimerArenas = object : CustomCountDownTimer(arenasTimeRemaining, 1) {
            override fun onTick(millisUntilFinished: Long) {
                viewModelScope.launch {
                    _timeRemainingArenas.emit(millisUntilFinished)
                }
            }

            override fun onFinish() {
                viewModelScope.launch {
//                        refreshMapData()
                }
            }
        }
        //  Don't run timers in tests, stops looper from idling
        if (!provideFakeTimers) {
            timerBr?.start()
            rankedTimerArenas?.start()
            unrankedTimerArenas?.start()
        } else {
            viewModelScope.launch {
                _timeRemainingArenas.emit(1657010700000)
                _timeRemainingArenasRanked.emit(1657011600000)
                _timeRemainingBr.emit(1657011600000)
            }
        }
    }

    fun checkTimers(calendar: Calendar) {
        val currentTimeMillis = calendar.timeInMillis
        if (timerBr != null && rankedTimerArenas != null && unrankedTimerArenas != null) {
            val brEndTime = timerBr!!.mMillisInFuture
            val rankedEndTime = rankedTimerArenas!!.mMillisInFuture
            val arenasEndTime = unrankedTimerArenas!!.mMillisInFuture
            if (
                (brEndTime) < currentTimeMillis ||
                (rankedEndTime < currentTimeMillis) ||
                arenasEndTime < currentTimeMillis
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