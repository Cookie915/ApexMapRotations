package com.example.apexmaprotations.viewmodels

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apexmaprotations.R
import com.example.apexmaprotations.models.Resource
import com.example.apexmaprotations.models.retrofit.CurrentMap
import com.example.apexmaprotations.models.retrofit.MapData
import com.example.apexmaprotations.repo.ApexRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ApexViewModel : ViewModel() {
    private val tag = "ApexViewModel"
    private val apexRepo = ApexRepo()

    private var mMapData = MutableStateFlow<Resource<MapData>>(Resource.Loading())
    val mapData: StateFlow<Resource<MapData>>
        get() = mMapData.asStateFlow()

    private var mTimeRemaining = MutableStateFlow<Long>(0)
    val timeRemaining: StateFlow<Long>
        get() = mTimeRemaining.asStateFlow()

    private var mShowMenu = MutableStateFlow<Boolean>(false)
    val showMenu: StateFlow<Boolean>
        get() = mShowMenu

    fun showMenu() {
        mShowMenu.value = true
    }

    fun hideMenu() {
        mShowMenu.value = false
    }

    private suspend fun initializeMapData() {
        apexRepo.getMapData().collect {
            when (it) {
                is Resource.Loading -> {
                    mMapData.value = Resource.Loading()
                }
                is Resource.Failure -> {
                    mMapData.value = Resource.Failure("Failed")
                }
                is Resource.Success -> {
                    Log.i(tag, "New Map Data")
                    mMapData.value = Resource.Success(it.data!!)
                    initializeTimer(it.data.currentMap)
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

    fun getKingCanyonImg(): Int {
        val images = listOf<Int>(
            R.drawable.transition_kings_canyon,
            R.drawable.transition_kings_canyon_mu1,
            R.drawable.transition_kings_canyon_mu2,
            R.drawable.transition_kings_canyon_mu3
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    fun getWorldsEdgeImg(): Int{
        val images = listOf<Int>(
            R.drawable.transition_world_s_edge,
            R.drawable.transition_world_s_edge_mu1,
            R.drawable.transition_world_s_edge_mu2,
            R.drawable.transition_world_s_edge_mu3
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    fun getOlympusImg(): Int{
        val images = listOf<Int>(
            R.drawable.transition_olympus,
            R.drawable.transition_olympus_mu1,
            R.drawable.transition_olympus_mu2
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    fun getStormPointImg(): Int{
        val images = listOf<Int>(
            R.drawable.transition_storm_point,
            R.drawable.transition_storm_point_mu1
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    init {
        viewModelScope.launch {
            initializeMapData()
        }
    }
}