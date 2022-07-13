package com.example.apexmaprotations.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.models.toStateFlow
import com.example.apexmaprotations.repo.ApexRepoImpl
import com.example.apexmaprotations.retrofit.MapDataBundle
import com.example.apexmaprotations.util.CustomCountdownTimer
import com.example.apexmaprotations.util.formatTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ApexViewModel"

@HiltViewModel
class ApexViewModel @Inject constructor(
    val apexRepo: ApexRepoImpl,
) : ViewModel() {
    private val _mapDataBundle: Flow<NetworkResult<MapDataBundle>> =
        apexRepo._mapData
    val mapDataBundle = _mapDataBundle
        .onStart { emit(NetworkResult.Loading()) }
        .conflate()
        .shareIn(viewModelScope, SharingStarted.Lazily, 0)

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
        }.toStateFlow(viewModelScope, listOf(0, "0.0", "0.0", 0))

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
        }.toStateFlow(viewModelScope, listOf("0.0", "0.0", 0))

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
        }.toStateFlow(viewModelScope, listOf("0.0", "0.0", 0))


    suspend fun refreshMapData() {
        _mapDataBundle.collect()
    }

    var timerBr: CustomCountdownTimer? = null
    var rankedTimerArenas: CustomCountdownTimer? = null
    var unrankedTimerArenas: CustomCountdownTimer? = null

    fun initializeTimer(mapData: MapDataBundle) {
        Log.i(TAG, "Init Timers")
        Log.i(TAG, "${mapData.battleRoyale.current.remainingSecs}")
        //  Prevent duplicate timers
        cancelTimers()
        Handler(Looper.getMainLooper())
            .post {
                val remainingTimeUnranked = mapData.arenas.current.remainingSecs * 1000L
                val remainingTimeRanked = mapData.arenasRanked.current.remainingSecs * 1000L
                val remainingTimeBr = mapData.battleRoyale.current.remainingSecs * 1000L

                timerBr = object : CustomCountdownTimer(remainingTimeBr, 1) {
                    override fun onTick(millisUntilFinished: Long) {
                        viewModelScope.launch {
                            _timeRemainingBr.emit(millisUntilFinished)
                        }
                    }

                    override suspend fun onFinish() {
                        delay(1500L)
                        refreshMapData()
                    }
                }

                rankedTimerArenas = object : CustomCountdownTimer(remainingTimeRanked, 1) {
                    override fun onTick(millisUntilFinished: Long) {
                        viewModelScope.launch {
                            _timeRemainingArenasRanked.emit(millisUntilFinished)
                        }
                    }

                    override suspend fun onFinish() {
                        delay(1500L)
                        refreshMapData()
                    }
                }

                unrankedTimerArenas = object : CustomCountdownTimer(remainingTimeUnranked, 1) {
                    override fun onTick(millisUntilFinished: Long) {
                        viewModelScope.launch {
                            _timeRemainingArenas.emit(millisUntilFinished)
                        }
                    }

                    override suspend fun onFinish() {
                        delay(1500L)
                        refreshMapData()
                    }
                }
                timerBr?.start()
                rankedTimerArenas?.start()
                unrankedTimerArenas?.start()
                checkTimers()
            }
    }

    override fun onCleared() {
        super.onCleared()
        cancelTimers()
        Log.i(TAG, "vm cleared")
    }

    private fun checkTimers() {
        val currentTime = System.currentTimeMillis()
        if (
            (timerBr?.mMillisInFutureToStop ?: 0) < currentTime ||
            ((rankedTimerArenas?.mMillisInFutureToStop ?: 0) < currentTime) ||
            (unrankedTimerArenas?.mMillisInFutureToStop ?: 0) < currentTime
        ) {
            viewModelScope.launch {
                refreshMapData()
            }
        }
    }

    private fun cancelTimers() {
        timerBr?.cancel(); timerBr = null
        rankedTimerArenas?.cancel(); rankedTimerArenas = null
        unrankedTimerArenas?.cancel(); unrankedTimerArenas = null
    }

}