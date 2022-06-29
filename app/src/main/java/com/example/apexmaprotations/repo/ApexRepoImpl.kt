package com.example.apexmaprotations.repo

import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.retrofit.MapDataBundle
import kotlinx.coroutines.flow.MutableSharedFlow

interface ApexRepoImpl {

    var _mapData: MutableSharedFlow<NetworkResult<MapDataBundle>>

    suspend fun refreshMapData()

    fun getKingsCanyonImage(): Int

    fun getWorldsEdgeImage(): Int

    fun getOlympusImage(): Int

    fun getStormPointImage(): Int

    fun getArenasImageForMapName(mapName: String): Int?

}