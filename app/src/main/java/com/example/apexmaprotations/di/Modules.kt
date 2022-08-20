package com.example.apexmaprotations.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.apexmaprotations.R
import com.example.apexmaprotations.data.retrofit.ApexApiInterceptor
import com.example.apexmaprotations.data.retrofit.ApexStatusApi
import com.example.apexmaprotations.repo.ApexRepo
import com.example.apexmaprotations.repo.ApexRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val USER_SETTINGS_DATASTORE = "Settings"

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {
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

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider {
        return DefaultDispatchers()
    }

    @Singleton
    @Provides
    fun provideRealApexRepo(
        apexApi: ApexStatusApi,
        dispatchers: DispatcherProvider
    ) = ApexRepo(apexApi, dispatchers) as ApexRepoImpl


    @Singleton
    @Provides
    fun provideSettingsDataStore(
        @ApplicationContext ctx: Context,
        dispatchers: DispatcherProvider
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(dispatchers.io + SupervisorJob()),
            produceFile = { ctx.preferencesDataStoreFile(USER_SETTINGS_DATASTORE) }
        )
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext ctx: Context
    ): SharedPreferences {
        return ctx.getSharedPreferences(
            ctx.getString(R.string.sharedPreferencesKey),
            Context.MODE_PRIVATE
        )
    }

}

//@Module
//@InstallIn(ViewModelComponent::class)
//object ViewModelModule {
//    @ViewModelScoped
//    @Provides
//    fun provideApexViewModel(apexRepo: ApexRepo): BattleRoyalViewModel {
//        return BattleRoyalViewModel(apexRepo)
//    }
//}


//@Module
//@InstallIn(ActivityRetainedComponent::class)
//object ActivityViewModelModule {
//    @ActivityRetainedScoped
//    @Provides
//    fun provideArenasViewModel(apexRepo: ApexRepo): ArenasViewModel {
//        return ArenasViewModel(apexRepo)
//    }
//
//    @ActivityRetainedScoped
//    @Provides
//    fun provideAppViewModel(): AppViewModel {
//        return AppViewModel()
//    }
//}
