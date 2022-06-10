package com.example.apexmaprotations.repo

import com.example.apexmaprotations.R
import com.example.apexmaprotations.models.retrofit.MapDataBundle
import com.example.apexmaprotations.models.retrofit.RetroFitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

private const val tag = "ApexRepo"

class ApexRepo {
    fun getMapDataStream(): Flow<MapDataBundle?> {
        return flow {
            emit(RetroFitInstance.apexApi.getMaps().body())
        }
    }

    fun getKingCanyonImg(): Int {
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
}