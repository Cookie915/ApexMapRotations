package com.example.apexmaprotations.di

import com.example.apexmaprotations.repo.ApexRepoImpl
import com.example.apexmaprotations.repo.FakeApexRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object TestRepositoryModule {
    //    @Provides
//    fun getFakeApi(): ApexStatusApi {
//        return Retrofit.Builder()
//            .baseUrl("/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApexStatusApi::class.java)
//    }
    @Provides
    fun getFakeRepo(): ApexRepoImpl {
        return FakeApexRepo()
    }
}