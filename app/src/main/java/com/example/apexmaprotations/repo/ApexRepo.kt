package com.example.apexmaprotations.repo

import android.util.Log
import com.example.apexmaprotations.data.models.BaseApiResponse
import com.example.apexmaprotations.data.models.NetworkResult
import com.example.apexmaprotations.data.retrofit.ApexStatusApi
import com.example.apexmaprotations.data.retrofit.MapDataBundle
import com.example.apexmaprotations.di.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

private const val TAG = "ApexRepo"

class ApexRepo @Inject constructor(
    private val apexApi: ApexStatusApi,
    dispatchers: DispatcherProvider
) : BaseApiResponse(), ApexRepoImpl {
    override var _mapData: Flow<NetworkResult<MapDataBundle>> = flow {
        Log.i(TAG, "Api Call")
        emit(NetworkResult.Loading())
        emit(safeApiCall { apexApi.getMapDataBundle() })
    }.flowOn(dispatchers.io)

    init {
        Log.i("ApexRepo", "Created Repo $this")
    }
}