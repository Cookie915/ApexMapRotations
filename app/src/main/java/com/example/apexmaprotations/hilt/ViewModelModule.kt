package com.example.apexmaprotations.hilt

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
    fun provideApexViewModel(): BattleRoyalViewModel {
        return BattleRoyalViewModel(
            apexRepo = ApexRepo()
        )
    }

    @Singleton
    @Provides
    fun provideArenasViewModel(): ArenasViewModel {
        return ArenasViewModel(
            apexRepo = ApexRepo()
        )
    }

    @Singleton
    @Provides
    fun provideAppViewModel(): AppViewModel {
        return AppViewModel()
    }

    @Singleton
    @Provides
    fun provideRepo(): ApexRepo {
        return ApexRepo()
    }


}