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
import com.example.apexmaprotations.models.Resource
import com.example.apexmaprotations.models.retrofit.ApexStatusApi
import com.example.apexmaprotations.models.retrofit.MapDataBundle
import com.example.apexmaprotations.models.toStateFlow
import com.example.apexmaprotations.repo.ApexRepo
import com.example.apexmaprotations.util.CustomCountdownTimer
import com.example.apexmaprotations.util.formatTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

@HiltViewModel
class BattleRoyalViewModel @Inject constructor(
    private val apexRepo: ApexRepo,
    private val apexStatusApi: ApexStatusApi
) : ViewModel() {
    private val tag = "BattleRoyalViewModel"
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var mMapDataBundle: Flow<Resource<MapDataBundle>> = apexRepo.getMapData()
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

    private var mCurrentMapImage = MutableStateFlow<Int?>(null)
    val currentMapImage: StateFlow<Int?>
        get() = mCurrentMapImage

    private var mNextMapImage = MutableStateFlow<Int?>(null)
    val nextMapImage: StateFlow<Int?>
        get() = mNextMapImage


    private fun initializeTimer(mapData: MapDataBundle) {
        Handler(Looper.getMainLooper())
            .post {
                val timeRemainingMillis = 5000L
//                    mapData.battleRoyale.current.remainingSecs * 1000L
                val timer = object : CustomCountdownTimer(timeRemainingMillis, 1) {
                    override fun onTick(millisUntilFinished: Long) {
                        mTimeRemaining.value = millisUntilFinished
                    }

                    override suspend fun onFinish() {
                        mMapDataBundle.collect() {
                            if (it is Resource.Success) {
                                initializeMapImages(it.data)
                                this.setMillisInFuture(it.data.battleRoyale.current.remainingSecs * 1000L)
                                this.start()
                            }
                        }
                    }
                }
                timer.start()
            }
    }


    private fun initializeMapImages(mapDataBundle: MapDataBundle) {
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

    init {
        viewModelScope.launch {
            mMapDataBundle.collect { mapData ->
                Log.i("tester", "Got New Maps Data from init")
                when (mapData) {
                    is Resource.Loading -> {
                        mCurrentMapImage.value = null
                        mNextMapImage.value = null
                    }
                    is Resource.Failure -> {
                        mCurrentMapImage.value = null
                        mNextMapImage.value = null
                    }
                    is Resource.Success -> {
                        initializeMapImages(mapData.data)
                        initializeTimer(mapData.data)
                    }
                }
            }
        }
    }

}