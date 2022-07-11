package com.example.apexmaprotations.repo

import android.util.Log
import com.example.apexmaprotations.R
import com.example.apexmaprotations.models.BaseApiResponse
import com.example.apexmaprotations.models.NetworkResult
import com.example.apexmaprotations.retrofit.ApexStatusApi
import com.example.apexmaprotations.retrofit.MapDataBundle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

private const val TAG = "ApexRepo"

class ApexRepo @Inject constructor(
    private val apexApi: ApexStatusApi
) : BaseApiResponse(), ApexRepoImpl {
    override var _mapData: Flow<NetworkResult<MapDataBundle>> = flow {
        Log.i(TAG, "Api Call")
        emit(safeApiCall { apexApi.getMapDataBundle() })
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

    init {
        Log.i("ApexRepo", "Created Repo $this")
    }
}