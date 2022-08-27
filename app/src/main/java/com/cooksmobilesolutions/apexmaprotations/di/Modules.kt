package com.cooksmobilesolutions.apexmaprotations.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.cooksmobilesolutions.apexmaprotations.R
import com.cooksmobilesolutions.apexmaprotations.repo.ApexRepo
import com.cooksmobilesolutions.apexmaprotations.repo.ApexRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val USER_SETTINGS_DATASTORE = "Settings"

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {
    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider {
        return DefaultDispatchers()
    }

    @Singleton
    @Provides
    fun provideRealApexRepo(
        dispatchers: DispatcherProvider
    ) = ApexRepo(dispatchers) as ApexRepoImpl


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