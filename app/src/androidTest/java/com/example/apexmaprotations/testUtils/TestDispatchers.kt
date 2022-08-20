package com.example.apexmaprotations.testUtils

import android.util.Log
import com.example.apexmaprotations.di.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatchers : DispatcherProvider {
    val testDispatcher = UnconfinedTestDispatcher(name = "TestDispatcherProvider Dispatcher")
    override val main: CoroutineDispatcher
        get() = testDispatcher
    override val io: CoroutineDispatcher
        get() = testDispatcher
    override val default: CoroutineDispatcher
        get() = testDispatcher

    init {
        Log.i("testerTest", "TestDispatchers Created ${this.testDispatcher.scheduler.hashCode()}")
    }
}