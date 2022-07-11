//package com.example.apexmaprotations.utils
//
//import android.app.Activity
//import android.app.AlarmManager
//import android.app.Application
//import android.content.Context
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.datastore.core.DataStore
//import androidx.datastore.core.DataStoreFactory
//import androidx.datastore.preferences.core.PreferenceDataStoreFactory
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.preferencesDataStoreFile
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.filters.MediumTest
//import androidx.test.platform.app.InstrumentationRegistry
//import app.cash.turbine.test
//import com.example.apexmaprotations.MainCoroutineRule
//import com.example.apexmaprotations.fragments.BattleRoyalFragment
//import com.example.apexmaprotations.fragments.TestApexFragmentFactory
//import com.example.apexmaprotations.launchFragmentInHiltContainer
//import com.example.apexmaprotations.testUtils.CoroutineTest
//import com.example.apexmaprotations.util.ALARM_TIME
//import com.example.apexmaprotations.util.dataStore
//import com.example.apexmaprotations.util.scheduleNotification
//import com.google.common.truth.Truth.assertThat
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import kotlinx.coroutines.*
//import kotlinx.coroutines.test.*
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import java.io.File
//import javax.inject.Inject
//
//@OptIn(ExperimentalCoroutinesApi::class)
//@MediumTest
//@HiltAndroidTest
//class ContextUtilsTests: CoroutineTest() {
//    @get:Rule(order = 0)
//    var hiltAndroidRule = HiltAndroidRule(this)
//
//    @get:Rule(order = 1)
//    var instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    private lateinit var dataStore: DataStore<Preferences>
//
//    @Inject
//    lateinit var fragmentFactory: TestApexFragmentFactory
//
//    @Before
//    fun setup() {
//        hiltAndroidRule.inject()
//    }
//    @Before
//    fun createDataStore() {
//
//    }
//
//
//
//    @After
//    fun teardown() {
//        //  Delete test file from disk
//        File(
//            ApplicationProvider.getApplicationContext<Context>().filesDir,
//            "datastore"
//        ).deleteRecursively()
//    }
//
//
//
//
//
//    @Test
//    fun contextScheduleNotification_setsAlarmClock() = runTest{
//        var activity: Activity? = null
//        launchFragmentInHiltContainer<BattleRoyalFragment>(fragmentFactory = fragmentFactory) {
//            activity  = this.requireActivity()
//            this.requireContext().scheduleNotification(true, 1000L, dataStore)
//        }
//        val alarmManager = activity?.getSystemService(AlarmManager::class.java) as AlarmManager
//        val nextAlarm = alarmManager.nextAlarmClock
//        assertThat(nextAlarm).isNotNull()
//    }
//
//    @Test
//    fun contextScheduleNotificationAlarm_setsDataStoreAlarmValue() = runTest{
//        val dataStoreScope = TestScope(StandardTestDispatcher() + Job())
//        var activity: Activity? = null
//        var scheduleTime: Long? = null
//        launchFragmentInHiltContainer<BattleRoyalFragment>(fragmentFactory = fragmentFactory) {
//            activity  = this.requireActivity()
//            scheduleTime = (System.currentTimeMillis() + 1000L).floorDiv(1000)
//            dataStoreScope.launch {
//                activity?.scheduleNotification(true, 1000L)
//            }
//        }
//        activity?.dataStore?.data?.test {
//            //  Skip initially stored data
//            skipItems(1)
//            //  Check alarm was set with scheduled time
//            assertThat(awaitItem()[ALARM_TIME]?.floorDiv(1000)).isEqualTo(scheduleTime)
//            cancelAndConsumeRemainingEvents()
//        }
//        dataStoreScope.cancel()
//    }
//}