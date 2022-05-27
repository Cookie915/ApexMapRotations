package com.example.apexmaprotations.viewmodels

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apexmaprotations.models.Resource
import com.example.apexmaprotations.models.retrofit.CurrentMap
import com.example.apexmaprotations.repo.ApexRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApexViewModel : ViewModel() {
    private val tag = "ApexViewModel"
    private val apexRepo = ApexRepo()

    private var mCurrentMap = MutableStateFlow<Resource<CurrentMap>>(Resource.Loading())
    val currentMap: StateFlow<Resource<CurrentMap>>
        get() = mCurrentMap.asStateFlow()

    private var  mTimeRemaining = MutableStateFlow<Long>(0)
    val timeRemaining: StateFlow<Long>
        get() = mTimeRemaining.asStateFlow()

    private suspend fun initializeMapData(){
        apexRepo.getMapData().collect {
            when (it) {
                is Resource.Loading -> {
                    mCurrentMap.value = Resource.Loading()
                }
                is Resource.Failure -> {
                    mCurrentMap.value = Resource.Failure("Failed")
                }
                is Resource.Success -> {
                    Log.i(tag, "New Map Data")
                    mCurrentMap.value = Resource.Success(it.data?.currentMap)
                    initializeTimer(it.data!!.currentMap)
                }
            }
        }
    }

    private fun initializeTimer(currentMap: CurrentMap){
        val remainingTime = currentMap.remainingSecs
        Log.i(tag, "Remaining Time: $remainingTime")
        Log.i(tag, "start Time: ${currentMap.start}")
        Log.i(tag, "End Time: ${currentMap.end}")
        CoroutineScope(Dispatchers.Main).launch{
            val timer = object : CountDownTimer(remainingTime * 1000L,1){
                override fun onTick(millisUntilFinished: Long) {
                    mTimeRemaining.value = millisUntilFinished
                }
                override fun onFinish() {
                    mTimeRemaining.value = 0L
                    CoroutineScope(Dispatchers.IO).launch {
                        initializeMapData()
                    }
                }
            }
            if (mTimeRemaining.value == 0L){
                timer.start()
            }
        }
    }

    init {
        viewModelScope.launch {
            initializeMapData()
        }
    }
}