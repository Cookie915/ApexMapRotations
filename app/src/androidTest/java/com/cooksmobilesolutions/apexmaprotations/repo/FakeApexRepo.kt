package com.cooksmobilesolutions.apexmaprotations.repo

import com.cooksmobilesolutions.apexmaprotations.data.models.MapData
import com.cooksmobilesolutions.apexmaprotations.data.models.NetworkResult
import com.cooksmobilesolutions.apexmaprotations.di.DispatcherProvider
import com.cooksmobilesolutions.apexmaprotations.testUtils.TestMapDataBundles
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FakeApexRepo(
    testDispatchers: DispatcherProvider
) : ApexRepoImpl {
    override var _mapData: Flow<NetworkResult<MapData>> = flow {
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