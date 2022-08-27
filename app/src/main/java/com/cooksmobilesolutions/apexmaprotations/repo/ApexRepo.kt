package com.cooksmobilesolutions.apexmaprotations.repo

import android.util.Log
import com.cooksmobilesolutions.apexmaprotations.data.models.MapData
import com.cooksmobilesolutions.apexmaprotations.data.models.NetworkResult
import com.cooksmobilesolutions.apexmaprotations.di.DispatcherProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

private const val TAG = "ApexRepo"

class ApexRepo @Inject constructor(
    dispatchers: DispatcherProvider
) : ApexRepoImpl {
    override var _mapData: Flow<NetworkResult<MapData>> = callbackFlow {
        trySend(NetworkResult.Loading())
        val mapDataDoc = FirebaseFirestore
            .getInstance()
            .collection("mapData")
            .document("mapData")
        val subscription =
            mapDataDoc.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    trySend(
                        NetworkResult.Error(
                            error.localizedMessage
                                ?: ("An unknown error occurred" + error.code)
                        )
                    )
                }
                if (snapshot!!.exists() && error == null) {
                    if (snapshot.metadata.isFromCache) {
                        trySend(NetworkResult.Loading<MapData>())
                    } else {
                        val mapData = snapshot.toObject(MapData::class.java)
                        trySend(NetworkResult.Success(mapData))
                    }
                } else {
                    trySend(
                        NetworkResult.Error<MapData>(
                            "Unable to connect to servers, please check you connections and try again!"
                        )
                    )
                }
            }
        awaitClose { subscription.remove() }
    }.flowOn(dispatchers.io)

    init {
        Log.i("ApexRepo", "Created Repo $this")
    }
}