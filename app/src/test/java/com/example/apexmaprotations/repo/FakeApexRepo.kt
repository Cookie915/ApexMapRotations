package com.example.apexmaprotations.repo

import com.example.apexmaprotations.R
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.retrofit.MapDataBundle
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class FakeApexRepo : ApexRepoImpl {
    override var _mapData: MutableStateFlow<NetworkResult<MapDataBundle>> =
        MutableStateFlow(NetworkResult.Loading())

    suspend fun emit(value: NetworkResult<MapDataBundle>) = _mapData.emit(value)

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun refreshMapData() {
        emit(NetworkResult.Loading())
        when (shouldReturnNetworkError) {
            true -> {
                emit(NetworkResult.Error(""))
            }
            false -> {
                emit(NetworkResult.Success(null))
            }
        }
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