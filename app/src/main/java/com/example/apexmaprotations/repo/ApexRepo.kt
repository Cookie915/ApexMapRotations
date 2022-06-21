package com.example.apexmaprotations.repo

import android.util.Log
import com.example.apexmaprotations.R
import com.example.apexmaprotations.models.BaseApiResponse
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.retrofit.ApexStatusApi
import com.example.apexmaprotations.retrofit.MapDataBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ApexRepo"

@Singleton
class ApexRepo @Inject constructor(
    private val apexApi: ApexStatusApi
) : BaseApiResponse() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var _mapData: MutableSharedFlow<NetworkResult<MapDataBundle>> = MutableSharedFlow()
    val mapData = _mapData.asSharedFlow()
        .stateIn(coroutineScope, SharingStarted.Lazily, NetworkResult.Loading())

    suspend fun refreshMapData() {
        Log.i(TAG, "Refreshing...")
        _mapData.emit(safeApiCall { apexApi.getMapDataBundle() })
    }

    fun getKingsCanyonImg(): Int {
        val images = listOf(
            R.drawable.kings_canyon_1,
            R.drawable.kings_canyon_2,
            R.drawable.kings_canyon_3
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    fun getWorldsEdgeImg(): Int {
        val images = listOf(
            R.drawable.worlds_edge_1,
            R.drawable.worlds_edge_2,
            R.drawable.worlds_edge_3
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    fun getOlympusImg(): Int {
        val images = listOf(
            R.drawable.olympus_2,
            R.drawable.olympus_3,
            R.drawable.transition_olympus,
            R.drawable.transition_olympus_mu1
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    fun getStormPointImg(): Int {
        val images = listOf(
            R.drawable.storm_point_1,
            R.drawable.storm_point_2
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    fun getArenasImageForMapName(mapName: String): Int? {
        when (mapName) {
            "Party crasher" -> {
                return R.drawable.party_crasher
            }
            "Phase runner" -> {
                return R.drawable.phase_runner
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


    init {
        coroutineScope.launch(Dispatchers.IO) {
            refreshMapData()
        }
        Log.i("ApexRepo", "Created Repo $this")
    }
}