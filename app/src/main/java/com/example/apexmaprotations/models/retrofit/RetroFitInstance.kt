package com.example.apexmaprotations.models.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetroFitInstance {
    val apexApi: ApexStatusApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mozambiquehe.re")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient()
                    .newBuilder()
                    .addInterceptor(ApexApiInterceptor())
                    .build()
            )
            .build()
            .create(ApexStatusApi::class.java)
    }
}