package com.example.apexmaprotations.di

import android.content.Context
import android.content.SharedPreferences
import com.example.apexmaprotations.R
import com.example.apexmaprotations.repo.ApexRepoImpl
import com.example.apexmaprotations.repo.FakeApexRepo
import com.example.apexmaprotations.testUtils.TestDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

private const val TEST_USER_SETTINGS_DATASTORE = "Test_Settings"

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SingletonModule::class]
)
class TestRepositoryModule {

    @Singleton
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return TestDispatchers()
    }

    @Singleton
    @Provides
    fun getFakeRepo(
        dispatchers: DispatcherProvider
    ): ApexRepoImpl {
        return FakeApexRepo(dispatchers)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.testSharedPreferencesKey),
            Context.MODE_PRIVATE
        )
    }

}