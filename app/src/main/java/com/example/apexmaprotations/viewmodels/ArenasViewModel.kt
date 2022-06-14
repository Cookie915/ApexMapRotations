package com.example.apexmaprotations.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArenasViewModel @Inject constructor(
    private val apexRepo: ApexRepo,
    private val apexApi: ApexStatusApi
) : ViewModel() {
    private var mMapDataBundle: Flow<Resource<MapDataBundle?>> = apexRepo.getMapData()
    val mapDataBundle: StateFlow<Resource<MapDataBundle?>>
        get() = mMapDataBundle.toStateFlow(viewModelScope, Resource.Loading)

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
                        mMapDataBundle.collect() {
                            if (it is Resource.Success && it.data != null) {
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
                        mMapDataBundle.collect() {
                            if (it is Resource.Success && it.data != null) {
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

    override fun onCleared() {
        super.onCleared()
        Log.i("tester2", "ArenaVM Cleared")
    }

    init {
        Log.i("tester2", "init arenasVM")
        viewModelScope.launch {
            mMapDataBundle.collect { mapData ->
                when (mapData) {
                    is Resource.Loading -> {
//                                mCurrentMapImage.value = null
//                                mNextMapImage.value = null
                    }
                    is Resource.Failure -> {
//                                mCurrentMapImage.value = null
//                                mNextMapImage.value = null
                    }
                    is Resource.Success -> {
//                        initializeMapImages(mapData.data)
                        if (mapData.data != null) {
                            initializeTimers(mapData.data)
                        }
                    }
                }
            }
        }
    }
}

