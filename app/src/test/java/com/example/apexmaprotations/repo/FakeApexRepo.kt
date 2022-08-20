package com.example.apexmaprotations.repo

import com.example.apexmaprotations.data.models.NetworkResult
import com.example.apexmaprotations.data.retrofit.MapDataBundle
import com.example.apexmaprotations.di.DispatcherProvider
import com.example.apexmaprotations.testUtils.androidTestMapDataBundles
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FakeApexRepo(
    testDispatchers: DispatcherProvider
) : ApexRepoImpl {
    override var _mapData: Flow<NetworkResult<MapDataBundle>> = flow {
        emit(NetworkResult.Loading())
        if (shouldReturnNetworkError) {
            emit(NetworkResult.Error("Error"))
        } else {
            emit(NetworkResult.Success(androidTestMapDataBundles.SuccessfulMapDataBundle))
        }
    }.flowOn(testDispatchers.io)

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }
}