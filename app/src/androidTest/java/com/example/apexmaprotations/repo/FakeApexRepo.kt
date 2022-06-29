package com.example.apexmaprotations.repo

import com.example.apexmaprotations.R
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.retrofit.MapDataBundle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class FakeApexRepo : ApexRepoImpl {
    override var _mapData: MutableSharedFlow<NetworkResult<MapDataBundle>> =
        MutableStateFlow(NetworkResult.Loading())

    suspend fun emit(value: NetworkResult<MapDataBundle>) = _mapData.emit(value)

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun refreshMapData() {
        emit(NetworkResult.Loading())
        when (shouldReturnNetworkError) {
            true -> {
                emit(NetworkResult.Error("Network Error"))
            }
            false -> {
                emit(NetworkResult.Success(null))
            }
        }
    }

    override fun getKingsCanyonImage(): Int {
        val images = listOf(
            R.drawable.kings_canyon_1,
            R.drawable.kings_canyon_2,
            R.drawable.kings_canyon_3
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    override fun getWorldsEdgeImage(): Int {
        val images = listOf(
            R.drawable.worlds_edge_1,
            R.drawable.worlds_edge_2,
            R.drawable.worlds_edge_3
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    override fun getOlympusImage(): Int {
        val images = listOf(
            R.drawable.olympus_2,
            R.drawable.olympus_3,
            R.drawable.transition_olympus,
            R.drawable.transition_olympus_mu1
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    override fun getStormPointImage(): Int {
        val images = listOf(
            R.drawable.storm_point_1,
            R.drawable.storm_point_2
        )
        val rand = Random()
        return images[rand.nextInt(images.size)]
    }

    override fun getArenasImageForMapName(mapName: String): Int? {
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
}