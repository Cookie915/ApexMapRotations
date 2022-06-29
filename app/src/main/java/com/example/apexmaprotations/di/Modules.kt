package com.example.apexmaprotations.di

import com.example.apexmaprotations.repo.ApexRepo
import com.example.apexmaprotations.repo.ApexRepoImpl
import com.example.apexmaprotations.retrofit.ApexApiInterceptor
import com.example.apexmaprotations.retrofit.ApexStatusApi
import com.example.apexmaprotations.viewmodels.AppViewModel
import com.example.apexmaprotations.viewmodels.ArenasViewModel
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
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
            .baseUrl(HttpUrl.get("https://api.mozambiquehe.re"))
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


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @ViewModelScoped
    @Provides
    fun provideApexViewModel(apexRepo: ApexRepo): BattleRoyalViewModel {
        return BattleRoyalViewModel(apexRepo)
    }
}


@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityViewModelModule {
    @ActivityRetainedScoped
    @Provides
    fun provideArenasViewModel(apexRepo: ApexRepo): ArenasViewModel {
        return ArenasViewModel(apexRepo)
    }

    @ActivityRetainedScoped
    @Provides
    fun provideAppViewModel(): AppViewModel {
        return AppViewModel()
    }
}


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideRealApexRepo(
        apexApi: ApexStatusApi
    ) = ApexRepo(apexApi) as ApexRepoImpl
}