package com.example.apexmaprotations.repo

import com.example.apexmaprotations.data.models.NetworkResult
import com.example.apexmaprotations.data.retrofit.MapDataBundle
import com.example.apexmaprotations.di.DispatcherProvider
import com.example.apexmaprotations.testUtils.TestMapDataBundles
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FakeApexRepo(
    testDispatchers: DispatcherProvider
) : ApexRepoImpl {
    override var _mapData: Flow<NetworkResult<MapDataBundle>> = flow {
        emit(NetworkResult.Loading())
        if (shouldReturnNetworkError) {
            emit(NetworkResult.Error("Network Error"))
        } else {
            emit(NetworkResult.Success(TestMapDataBundles.SuccessfulMapDataBundle))
        }
    }.flowOn(testDispatchers.io)

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }
}