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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ArenaViewModel"
@HiltViewModel
class ArenasViewModel @Inject constructor(
    private val apexRepo: ApexRepoImpl
) : ViewModel() {
    val mapDataBundle: StateFlow<NetworkResult<MapDataBundle>> =
        apexRepo._mapData
            .stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily, NetworkResult.Loading())

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

    private var mCurrentMapImage = MutableStateFlow<Int?>(null)
    val currentMapImage: StateFlow<Int?>
        get() = mCurrentMapImage

    private var mNextMapImage = MutableStateFlow<Int?>(null)
    val nextMapImage: StateFlow<Int?>
        get() = mNextMapImage

    private var mCurrentMapImageRanked = MutableStateFlow<Int?>(null)
    val currentMapImageRanked: StateFlow<Int?>
        get() = mCurrentMapImageRanked

    private var mNextMapImageRanked = MutableStateFlow<Int?>(null)
    val nextMapImageRanked: StateFlow<Int?>
        get() = mNextMapImageRanked

    private var rankedTimer: CustomCountdownTimer? = null
    private var unrankedTimer: CustomCountdownTimer? = null

    suspend fun refreshMapData() {
        apexRepo.refreshMapData()
    }

    private fun initializeTimers(mapData: MapDataBundle) {
        Handler(Looper.getMainLooper())
            .post {
                //  Prevent duplicate timers
                rankedTimer?.cancel()
                unrankedTimer?.cancel()
                rankedTimer = null
                unrankedTimer = null
                val remainingTimeUnranked = mapData.arenas.current.remainingSecs * 1000L
                val remainingTimeRanked = mapData.arenasRanked.current.remainingSecs * 1000L
                rankedTimer = object : CustomCountdownTimer(remainingTimeRanked, 1) {
                    override fun onTick(millisUntilFinished: Long) {
                        mTimeRemainingRanked.value = millisUntilFinished
                    }

                    override suspend fun onFinish() {
                        delay(1500L)
                        refreshMapData()
                    }
                }
                unrankedTimer = object : CustomCountdownTimer(remainingTimeUnranked, 1) {
                    override fun onTick(millisUntilFinished: Long) {
                        mTimeRemainingUnranked.value = millisUntilFinished
                    }

                    override suspend fun onFinish() {
                        delay(1500L)
                        refreshMapData()
                    }
                }
                rankedTimer?.start()
                unrankedTimer?.start()
            }
    }

    init {
        Log.i(TAG, "init")
        viewModelScope.launch {
            mapDataBundle.collectLatest { mapData ->
                when (mapData) {
                    is NetworkResult.Loading -> {
                        Log.i(TAG, "Loading")
                    }
                    is NetworkResult.Error -> {
                        Log.i(TAG, "Error")
                        //  Rate limit hit, wait and re-fetch data
                        if (mapData.message == "429") {
                            delay(2100L)
                            refreshMapData()
                        }
                    }
                    is NetworkResult.Success -> {
                        Log.i(TAG, "Success")
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

    fun initializeMapImages(mapData: MapDataBundle) {
        mCurrentMapImageRanked.value =
            apexRepo.getArenasImageForMapName(mapData.arenasRanked.current.map)
        mNextMapImageRanked.value = apexRepo.getArenasImageForMapName(mapData.arenasRanked.next.map)
        mCurrentMapImage.value = apexRepo.getArenasImageForMapName(mapData.arenas.current.map)
        mNextMapImage.value = apexRepo.getArenasImageForMapName(mapData.arenas.next.map)
    }
}

