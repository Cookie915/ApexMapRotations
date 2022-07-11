package com.example.apexmaprotations.repo

import com.example.apexmaprotations.R
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.retrofit.MapDataBundle
import com.example.apexmaprotations.testUtils.TestMapDataBundles
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class FakeApexRepo : ApexRepoImpl {
    override var _mapData: Flow<NetworkResult<MapDataBundle>> = flow {
        if (shouldReturnNetworkError) {
            emit(NetworkResult.Error("Network Error"))
        } else {
            emit(NetworkResult.Success(TestMapDataBundles.SuccessfulMapDataBundle))
        }
    }

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override fun getRandomBgImage(): Int {
        val bgImages = listOf(
            R.drawable.bg_ash,
            R.drawable.bg_bloodhound,
            R.drawable.bg_wraith,
            R.drawable.bg_octane,
            R.drawable.bg_valk,
            R.drawable.bg_pathy
        )
        val rand = Random()
        return bgImages[rand.nextInt(bgImages.size)]
    }
}