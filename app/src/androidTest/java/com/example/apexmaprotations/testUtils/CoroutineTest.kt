package com.example.apexmaprotations.testUtils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

//  https://www.wwt.com/article/testing-android-datastore
@ExperimentalCoroutinesApi
abstract class CoroutineTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    //  Protected to allow access in test classes
    @OptIn(ExperimentalCoroutinesApi::class)
    protected val testDispatcher = StandardTestDispatcher()
    protected val testCoroutineScope = TestScope(testDispatcher)

    @Before
    fun setupCoroutineTest() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardownCoroutineTest() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        testCoroutineScope.cancel()
    }
}