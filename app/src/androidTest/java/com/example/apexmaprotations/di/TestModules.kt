package com.example.apexmaprotations.di

import com.example.apexmaprotations.retrofit.ApexStatusApi
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApexStatusApi::class]
)
object TestModules {
    @Provides
    fun getFakeApi(): ApexStatusApi {
        return Retrofit.Builder()
            .baseUrl("/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApexStatusApi::class.java)
    }
}