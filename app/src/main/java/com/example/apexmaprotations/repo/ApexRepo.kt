package com.example.apexmaprotations.repo

import android.util.Log
import com.example.apexmaprotations.R
import com.example.apexmaprotations.models.BaseApiResponse
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.models.toStateFlow
import com.example.apexmaprotations.retrofit.ApexStatusApi
import com.example.apexmaprotations.retrofit.MapDataBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val tag = "ApexRepo"

@Singleton
class ApexRepo @Inject constructor(
    private val apexApi: ApexStatusApi
) : BaseApiResponse() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var _mapData: Flow<NetworkResult<MapDataBundle>> = getMapData()
    val mapData: StateFlow<NetworkResult<MapDataBundle>> by lazy {
        _mapData.toStateFlow(coroutineScope, NetworkResult.Loading())
    }

    fun refreshMapData() {
        coroutineScope.launch {
            _mapData.collect()
        }
    }

    private fun getMapData(): Flow<NetworkResult<MapDataBundle>> {
        return flow {
            emit(safeApiCall { apexApi.getMapDataBundle() })
        }
            .flowOn(Dispatchers.IO)
    }

    fun getKingsCanyonImg(): Int {
        val images = listOf(
            R.drawable.transition_kings_canyon,
            R.drawable.transition_kings_canyon_mu1,
            R.drawable.transition_kings_canyon_mu2,
            R.drawable.transition_kings_canyon_mu3
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    fun getWorldsEdgeImg(): Int {
        val images = listOf(
            R.drawable.transition_world_s_edge,
            R.drawable.transition_world_s_edge_mu1,
            R.drawable.transition_world_s_edge_mu2,
            R.drawable.transition_world_s_edge_mu3
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    fun getOlympusImg(): Int {
        val images = listOf(
            R.drawable.transition_olympus,
            R.drawable.transition_olympus_mu1,
            R.drawable.transition_olympus_mu2
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    fun getStormPointImg(): Int {
        val images = listOf(
            R.drawable.transition_storm_point,
            R.drawable.transition_storm_point_mu1
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    fun getPartyCrasherImage(): Int {
        return R.drawable.party_crash
    }

    fun getPhaseRunnerImage(): Int {
        return R.drawable.phase_rush
    }

    fun getEncoreImage(): Int {
        return R.drawable.encore
    }

    fun getHabitatImage(): Int {
        return R.drawable.habitat
    }

    fun getDropOffImage(): Int {
        return R.drawable.bg_drop_off
    }

    init {
        Log.i("ApexRepo", "Created Repo $this")
    }
}