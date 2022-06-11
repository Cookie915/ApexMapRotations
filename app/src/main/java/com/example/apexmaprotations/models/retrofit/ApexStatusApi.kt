package com.example.apexmaprotations.models.retrofit
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET


interface ApexStatusApi {
    @GET("/maprotation")
    fun getMaps(): Call<Response<MapDataBundle>>
}