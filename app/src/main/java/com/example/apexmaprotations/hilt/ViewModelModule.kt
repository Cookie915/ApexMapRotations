package com.example.apexmaprotations.hilt

import com.example.apexmaprotations.repo.ApexRepo
import com.example.apexmaprotations.retrofit.ApexStatusApi
import com.example.apexmaprotations.viewmodels.AppViewModel
import com.example.apexmaprotations.viewmodels.ArenasViewModel
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @ViewModelScoped
    @Provides
    fun provideApexViewModel(apexRepo: ApexRepo): BattleRoyalViewModel {
        return BattleRoyalViewModel(apexRepo)
    }

    @ViewModelScoped
    @Provides
    fun provideArenasViewModel(apexRepo: ApexRepo): ArenasViewModel {
        return ArenasViewModel(apexRepo)
    }

    @ViewModelScoped
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
    fun provideRepo(apexApi: ApexStatusApi): ApexRepo {
        return ApexRepo(apexApi)
    }
}