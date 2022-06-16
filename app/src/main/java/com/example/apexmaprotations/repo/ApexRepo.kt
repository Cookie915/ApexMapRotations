package com.example.apexmaprotations.repo

import com.example.apexmaprotations.R
import com.example.apexmaprotations.models.Resource
import com.example.apexmaprotations.models.asResource
import com.example.apexmaprotations.models.retrofit.ApexStatusApi
import com.example.apexmaprotations.models.retrofit.MapDataBundle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

private const val tag = "ApexRepo"

class ApexRepo(
    private val apexApi: ApexStatusApi
) {

    fun getMapData(): Flow<Resource<MapDataBundle>> {
        return flow {
            val mapDataBundle = apexApi.getMapDataBundle()
            if (mapDataBundle.isSuccessful && mapDataBundle.body() != null) {
                emit(mapDataBundle.body()!!)
            } else {
                throw Throwable(mapDataBundle.errorBody().toString())
            }
        }.asResource()
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
}