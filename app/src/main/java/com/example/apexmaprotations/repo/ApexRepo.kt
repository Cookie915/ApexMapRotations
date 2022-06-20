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

    suspend fun refreshMapData() {
        Log.i("ArenaViewModel", "Refreshing...")
        _mapData.collect()
    }

    private fun getMapData(): Flow<NetworkResult<MapDataBundle>> {
        return flow {
            emit(safeApiCall { apexApi.getMapDataBundle() })
        }
            .flowOn(Dispatchers.IO)
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

    init {
        Log.i("ApexRepo", "Created Repo $this")
    }
}