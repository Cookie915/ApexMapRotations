package com.example.apexmaprotations.hilt

import com.example.apexmaprotations.models.retrofit.ApexStatusApi
import com.example.apexmaprotations.repo.ApexRepo
import com.example.apexmaprotations.viewmodels.AppViewModel
import com.example.apexmaprotations.viewmodels.ArenasViewModel
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {
    @Singleton
    @Provides
    fun provideApexViewModel(apexRepo: ApexRepo, apexApi: ApexStatusApi): BattleRoyalViewModel {
        return BattleRoyalViewModel(apexRepo, apexApi)
    }

    @Singleton
    @Provides
    fun provideArenasViewModel(apexRepo: ApexRepo, apexApi: ApexStatusApi): ArenasViewModel {
        return ArenasViewModel(apexRepo, apexApi)
    }

    @Singleton
    @Provides
    fun provideAppViewModel(): AppViewModel {
        return AppViewModel()
    }

    @Singleton
    @Provides
    fun provideRepo(apexApi: ApexStatusApi): ApexRepo {
        return ApexRepo(apexApi)
    }


}