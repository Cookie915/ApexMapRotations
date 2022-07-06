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
import com.example.apexmaprotations.repo.ApexRepoImpl
import com.example.apexmaprotations.retrofit.MapDataBundle
import com.example.apexmaprotations.util.CustomCountdownTimer
import com.example.apexmaprotations.util.formatTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
private const val TAG = "BattleRoyalViewModel"

@HiltViewModel
class BattleRoyalViewModel @Inject constructor(
    private val apexRepo: ApexRepoImpl,
) : ViewModel() {
    val mapDataBundle: StateFlow<NetworkResult<MapDataBundle>> =
        apexRepo._mapData.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            NetworkResult.Loading()
        )

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

    suspend fun refreshMapData() {
        apexRepo.refreshMapData()
    }

    private var timer: CustomCountdownTimer? = null

    fun initializeTimer(mapData: MapDataBundle) {
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
                mCurrentMapImage.value = apexRepo.getKingsCanyonImage()
            }
            "Olympus" -> {
                mCurrentMapImage.value = apexRepo.getOlympusImage()
            }
            "World's Edge" -> {
                mCurrentMapImage.value = apexRepo.getWorldsEdgeImage()
            }
            "Storm Point" -> {
                mCurrentMapImage.value = apexRepo.getStormPointImage()
            }
        }
        when (mapDataBundle.battleRoyale.next.map) {
            "King's Canyon" -> {
                mNextMapImage.value = apexRepo.getKingsCanyonImage()
            }
            "Olympus" -> {
                mNextMapImage.value = apexRepo.getOlympusImage()
            }
            "World's Edge" -> {
                mNextMapImage.value = apexRepo.getWorldsEdgeImage()
            }
            "Storm Point" -> {
                mNextMapImage.value = apexRepo.getStormPointImage()
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
                    }
                    is NetworkResult.Error -> {
                        Log.i(TAG, "FetchMapDataFaiL ${mapData.message}")
                        //  Rate limit hit, wait and re-fetch data
                        if (mapData.message == "429") {
                            delay(3000L)
                            refreshMapData()
                        }
                    }
                    is NetworkResult.Success -> {
                        Log.i(TAG, "FetchMapData Success")
                        if (mapData.data != null) {
                            initializeTimer(mapData.data)
                        }
                    }
                }
            }
        }
    }
}