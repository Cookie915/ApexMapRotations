package com.example.apexmaprotations.retrofit
import retrofit2.Response
import retrofit2.http.GET


interface ApexStatusApi {
    @GET("/maprotation")
    suspend fun getMapDataBundle(): Response<MapDataBundle>
}