package com.example.apexmaprotations.repo

import com.example.apexmaprotations.data.models.NetworkResult
import com.example.apexmaprotations.data.retrofit.MapDataBundle
import kotlinx.coroutines.flow.Flow

interface ApexRepoImpl {
    var _mapData: Flow<NetworkResult<MapDataBundle>>
}