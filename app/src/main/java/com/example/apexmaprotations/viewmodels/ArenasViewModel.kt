package com.example.apexmaprotations.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apexmaprotations.R
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.models.toStateFlow
import com.example.apexmaprotations.repo.ApexRepo
import com.example.apexmaprotations.retrofit.MapDataBundle
import com.example.apexmaprotations.util.CustomCountdownTimer
import com.example.apexmaprotations.util.formatTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ArenaViewModel"
@HiltViewModel
class ArenasViewModel @Inject constructor(
    private val apexRepo: ApexRepo
) : ViewModel() {
    val mapDataBundle: StateFlow<NetworkResult<MapDataBundle>> = apexRepo.mapData

    //  Ranked Timer
    private var mTimeRemainingUnranked = MutableStateFlow<Long>(0)
    val timeRemainingUnranked: StateFlow<List<Any>>
        get() = mTimeRemainingUnranked.transform {
            val minutes = it / (1000 * 60) % 60
            val seconds = it / (1000) % 60
            val decimal = it / 100 % 10
            val times = formatTime(minutes, seconds)
            val list = listOf<Any>(times.first(), times.last(), decimal)
            emit(list)
        }.toStateFlow(viewModelScope, listOf("0.0", "0.0", 0))
    val timeRemainingUnrankedLong: StateFlow<Long>
        get() = mTimeRemainingUnranked

    //  Unranked Timer
    private var mTimeRemainingRanked = MutableStateFlow<Long>(0)
    val timeRemainingRanked: StateFlow<List<Any>>
        get() = mTimeRemainingRanked.transform {
            val minutes = it / (1000 * 60) % 60
            val seconds = it / (1000) % 60
            val decimal = it / 100 % 10
            val times = formatTime(minutes, seconds)
            val list = listOf<Any>(times.first(), times.last(), decimal)
            emit(list)
        }.toStateFlow(viewModelScope, listOf("0.0", "0.0", 0))
    val timeRemainingRankedLong: StateFlow<Long>
        get() = mTimeRemainingRanked

    private var mCurrentMapImage = MutableStateFlow<Int?>(null)
    val currentMapImage: StateFlow<Int?>
        get() = mCurrentMapImage

    private var mNextMapImage = MutableStateFlow<Int?>(null)
    val nextMapImage: StateFlow<Int?>
        get() = mNextMapImage

    private var mCurrentMapImageRanked = MutableStateFlow<Int?>(null)
    val currentMapImageRanked: StateFlow<Int?>
        get() = mCurrentMapImage

    private var mNextMapImageRanked = MutableStateFlow<Int?>(null)
    val nextMapImageRanked: StateFlow<Int?>
        get() = mNextMapImage

    private fun refreshMapData() {
        apexRepo.refreshMapData()
    }

    private fun initializeTimers(mapData: MapDataBundle) {
        Handler(Looper.getMainLooper())
            .post {
                val remainingTimerUnranked = mapData.arenas.current.remainingSecs * 1000L
                val remainingTimerRanked = mapData.arenasRanked.current.remainingSecs * 1000L
                val rankedTimer = object : CustomCountdownTimer(remainingTimerRanked, 1) {
                    override fun onTick(millisUntilFinished: Long) {
                        mTimeRemainingRanked.value = millisUntilFinished
                    }

                    override suspend fun onFinish() {
                        refreshMapData()
                        mapDataBundle.collect() {
                            if (it is NetworkResult.Success && it.data != null) {
                                initializeMapImages(it.data)
                                this.setMillisInFuture(it.data.arenasRanked.current.remainingSecs * 1000L)
                                this.start()
                            }
                        }
                    }
                }
                val unRankedTimer = object : CustomCountdownTimer(remainingTimerUnranked, 1) {
                    override fun onTick(millisUntilFinished: Long) {
                        mTimeRemainingUnranked.value = millisUntilFinished
                    }

                    override suspend fun onFinish() {
                        refreshMapData()
                        mapDataBundle.collect() {
                            if (it is NetworkResult.Success && it.data != null) {
                                initializeMapImages(it.data)
                                this.setMillisInFuture(it.data.arenas.current.remainingSecs * 1000L)
                                this.start()
                            }
                        }
                    }
                }
                rankedTimer.start()
                unRankedTimer.start()
            }
    }

    init {
        Log.i(TAG, "init")
        viewModelScope.launch {
            mapDataBundle.collect() { mapData ->
                when (mapData) {
                    is NetworkResult.Loading -> {}
                    is NetworkResult.Error -> {
                        //  Rate limit hit, wait and re-fetch data
                        if (mapData.message == "429") {
                            delay(2100L)
                            refreshMapData()
                        }
                    }
                    is NetworkResult.Success -> {
                        if (mapData.data != null) {
                            initializeMapImages(mapData.data)
                            initializeTimers(mapData.data)
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "ArenaVM Cleared")
    }

    private fun initializeMapImages(mapData: MapDataBundle) {
        mCurrentMapImageRanked.value = getImageForMapName(mapData.arenasRanked.current.map)
        mNextMapImageRanked.value = getImageForMapName(mapData.arenasRanked.next.map)
        mCurrentMapImage.value = getImageForMapName(mapData.arenas.current.map)
        mNextMapImage.value = getImageForMapName(mapData.arenas.next.map)
    }

    private fun getImageForMapName(mapName: String): Int? {
        Log.i("tester6", mapName)
        when (mapName) {
            "Party crasher" -> {
                return R.drawable.party_crash
            }
            "Phase runner" -> {
                return R.drawable.phase_rush
            }
            "Overflow" -> {
                return R.drawable.overflow
            }
            "Encore" -> {
                return R.drawable.encore
            }
            "Habitat" -> {
                return R.drawable.habitat
            }
            "Drop Off" -> {
                return R.drawable.bg_drop_off
            }
            else -> {
                return null
            }
        }
    }
}

