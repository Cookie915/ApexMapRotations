package com.example.apexmaprotations.hilt

import com.example.apexmaprotations.retrofit.ApexApiInterceptor
import com.example.apexmaprotations.retrofit.ApexStatusApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetroFitModule {
    @Singleton
    @Provides
    fun provideRetrofitInstance(): ApexStatusApi {
        return Retrofit.Builder()
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
