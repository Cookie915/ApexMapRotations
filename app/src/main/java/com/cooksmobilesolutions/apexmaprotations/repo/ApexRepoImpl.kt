package com.cooksmobilesolutions.apexmaprotations.repo

import com.cooksmobilesolutions.apexmaprotations.data.models.MapData
import com.cooksmobilesolutions.apexmaprotations.data.models.NetworkResult
import kotlinx.coroutines.flow.Flow

interface ApexRepoImpl {
    var _mapData: Flow<NetworkResult<MapData>>
}