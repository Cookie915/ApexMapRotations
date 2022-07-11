package com.example.apexmaprotations.repo

import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.retrofit.MapDataBundle
import kotlinx.coroutines.flow.Flow

interface ApexRepoImpl {
    var _mapData: Flow<NetworkResult<MapDataBundle>>
    fun getRandomBgImage(): Int
}