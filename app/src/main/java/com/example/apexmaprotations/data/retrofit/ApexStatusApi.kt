package com.example.apexmaprotations.data.retrofit
import retrofit2.Response
import retrofit2.http.GET


interface ApexStatusApi {
    @GET("/maprotation")
    suspend fun getMapDataBundle(): Response<MapDataBundle>
}