package com.example.apexmaprotations.viewmodels

import android.content.Context
import android.os.CountDownTimer
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

@HiltViewModel
class BattleRoyalViewModel @Inject constructor(
    private val apexRepo: ApexRepo
) : ViewModel() {
    private val tag = "BattleRoyalViewModel"

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

    private var mCurrentMapImage = MutableStateFlow<Int?>(null)
    val currentMapImage: StateFlow<Int?>
        get() = mCurrentMapImage


    fun initializeTimer(currentMap: MapDataBundle.BattleRoyale) {
        val call = RetroFitInstance.apexApi.getMaps()
        val remainingTimer = currentMap.current.remainingSecs
        CoroutineScope(Dispatchers.Main).launch {
            val timer = object : CountDownTimer(remainingTimer * 1000L, 1) {
                override fun onTick(millisUntilFinished: Long) {
                    mTimeRemaining.value = millisUntilFinished
                }

                override fun onFinish() {
                    viewModelScope.launch {
                        call.enqueue(object : Callback<Response<MapDataBundle>> {
                            override fun onResponse(
                                call: Call<Response<MapDataBundle>>,
                                response: Response<Response<MapDataBundle>>
                            ) {
                                TODO("Not yet implemented")
                            }

                            override fun onFailure(
                                call: Call<Response<MapDataBundle>>,
                                t: Throwable
                            ) {
                                TODO("Not yet implemented")
                            }

                        })
                        RetroFitInstance.apexApi.getMaps()
                        mMapDataBundle.collectLatest { dataBundle ->
                            if (dataBundle is Resource.Success) {
                                RetroFitInstance.apexApi.getMaps()
                                initializeTimer(dataBundle.data!!.battleRoyale)
                            }
                            mTimeRemaining.value = 0L
                        }
                    }
                }
            }
            if (mTimeRemaining.value == 0L) {
                timer.start()
            }
        }
    }


    fun getKingCanyonImg(): Int {
        return apexRepo.getKingCanyonImg()
    }

    fun getWorldsEdgeImg(): Int {
        return apexRepo.getWorldsEdgeImg()
    }

    fun getOlympusImg(): Int {
        return apexRepo.getOlympusImg()
    }

    fun getStormPointImg(): Int {
        return apexRepo.getStormPointImg()
    }
}