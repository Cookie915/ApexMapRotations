package com.example.apexmaprotations.viewmodels

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
private const val TAG = "BattleRoyalViewModel"

@HiltViewModel
class BattleRoyalViewModel @Inject constructor(
    private val apexRepo: ApexRepo,
) : ViewModel() {
    val mapDataBundle: StateFlow<NetworkResult<MapDataBundle>> =
        apexRepo.mapData

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
    private var mCurrentMapImage = MutableStateFlow<Int?>(null)
    val currentMapImage: StateFlow<Int?>
        get() = mCurrentMapImage
    private var mNextMapImage = MutableStateFlow<Int?>(null)
    val nextMapImage: StateFlow<Int?>
        get() = mNextMapImage

    private suspend fun refreshMapData() {
        apexRepo.refreshMapData()
    }

    private var timer: CustomCountdownTimer? = null

    private fun initializeTimer(mapData: MapDataBundle) {
        Handler(Looper.getMainLooper())
            .post {
                //  Prevent duplicate timers
                timer?.cancel()
                timer = null
                val timeRemainingMillis = mapData.battleRoyale.current.remainingSecs * 1000L
                timer = object : CustomCountdownTimer(timeRemainingMillis, 1) {
                    override fun onTick(millisUntilFinished: Long) {
                        mTimeRemaining.value = millisUntilFinished
                    }

                    override suspend fun onFinish() {
                        delay(1500L)
                        refreshMapData()
                    }
                }
                timer?.start()
            }
    }

    private fun assignMapImages(mapDataBundle: MapDataBundle) {
        when (mapDataBundle.battleRoyale.current.map) {
            "King's Canyon" -> {
                mCurrentMapImage.value = apexRepo.getKingsCanyonImg()
            }
            "Olympus" -> {
                mCurrentMapImage.value = apexRepo.getOlympusImg()
            }
            "World's Edge" -> {
                mCurrentMapImage.value = apexRepo.getWorldsEdgeImg()
            }
            "Storm Point" -> {
                mCurrentMapImage.value = apexRepo.getStormPointImg()
            }
        }
        when (mapDataBundle.battleRoyale.next.map) {
            "King's Canyon" -> {
                mNextMapImage.value = apexRepo.getKingsCanyonImg()
            }
            "Olympus" -> {
                mNextMapImage.value = apexRepo.getOlympusImg()
            }
            "World's Edge" -> {
                mNextMapImage.value = apexRepo.getWorldsEdgeImg()
            }
            "Storm Point" -> {
                mNextMapImage.value = apexRepo.getStormPointImg()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "vm cleared")
    }

    init {
        Log.i(TAG, "Vm Init $this")
        viewModelScope.launch {
            mapDataBundle.collectLatest { mapData ->
                Log.i(TAG, "Got New Maps Data from init")
                when (mapData) {
                    is NetworkResult.Loading -> {
                        Log.i(TAG, "Loading...")
                        mCurrentMapImage.value = null
                        mNextMapImage.value = null
                    }
                    is NetworkResult.Error -> {
                        Log.i(TAG, "FetchMapDataFaiL ${mapData.message}")
                        //  Rate limit hit, wait and re-fetch data
                        if (mapData.message == "429") {
                            delay(2100L)
                            refreshMapData()
                        }
                        mCurrentMapImage.value = null
                        mNextMapImage.value = null
                    }
                    is NetworkResult.Success -> {
                        Log.i(TAG, "FetchMapData Success")
                        if (mapData.data != null) {
                            assignMapImages(mapData.data)
                            initializeTimer(mapData.data)
                        }
                    }
                }
            }
        }
    }
}