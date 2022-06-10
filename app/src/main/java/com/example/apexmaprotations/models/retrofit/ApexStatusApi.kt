package com.example.apexmaprotations.models.retrofit
import retrofit2.Response
import retrofit2.http.GET


interface ApexStatusApi {
    @GET("/maprotation")
    suspend fun getMaps(): Response<MapDataBundle>
}