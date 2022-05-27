package com.example.apexmaprotations.repo

import android.util.Log
import com.example.apexmaprotations.models.Resource
import com.example.apexmaprotations.models.retrofit.MapData
import com.example.apexmaprotations.models.retrofit.RetroFitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

private const val tag = "ApexRepo"

class ApexRepo {
    suspend fun getMapData(): Flow<Resource<MapData>> = callbackFlow {
        trySend(Resource.Loading())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = RetroFitInstance.apexApi.getMaps()
                when (data.isSuccessful) {
                    false -> {
                        trySend(Resource.Failure(data.code().toString()))
                    }
                    true -> {
                        Log.i(tag, "New Map Data")
                        trySend(Resource.Success(data.body()))
                    }
                }
            } catch (err: Exception){
                err.localizedMessage?.let { Log.i(tag, it) }
                trySend(Resource.Failure(err.localizedMessage ?: "Error Accessing Api"))
            }
        }
        awaitClose {
            channel.close()
        }
    }
}