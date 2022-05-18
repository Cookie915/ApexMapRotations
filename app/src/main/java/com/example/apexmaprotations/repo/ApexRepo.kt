package com.example.apexmaprotations.repo

import com.example.apexmaprotations.models.retrofit.RetroFitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.apexmaprotations.models.Resource
import com.example.apexmaprotations.models.retrofit.MapData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow



class ApexRepo {
    suspend fun getMapData(): Flow<Resource<MapData>> = callbackFlow {
        trySend(Resource.Loading())
        CoroutineScope(Dispatchers.IO).launch {
            val data = RetroFitInstance.apexApi.getMaps()
            trySend(Resource.Success(data.body()))
        }
        awaitClose {
        }
    }
}