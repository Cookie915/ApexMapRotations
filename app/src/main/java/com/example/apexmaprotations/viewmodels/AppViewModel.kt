package com.example.apexmaprotations.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apexmaprotations.R
import com.example.apexmaprotations.alarm.AlarmBroadCastReceiver
import com.example.apexmaprotations.di.DispatcherProvider
import com.example.apexmaprotations.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    val sharedPreferences: SharedPreferences
) : ViewModel() {
    private var mShowSplash = MutableStateFlow(true)
    val showSplash: StateFlow<Boolean>
        get() = mShowSplash

    private var _backgroundImage = MutableStateFlow<Int?>(null)
    val backgroundImage
        get() = _backgroundImage

    private var _errStatus: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val errStatus: StateFlow<Boolean>
        get() = _errStatus

    private var _errImage = MutableStateFlow<Int?>(null)
    val errImage
        get() = _errImage

    private var _errMessage = MutableStateFlow<String?>(null)
    val errMessage
        get() = _errMessage

    private var _isAlarmSet = MutableStateFlow(sharedPreferences.getLong(ALARM_TIME_KEY, 0) > 0)
    val isAlarmSet
        get() = _isAlarmSet

    private var _isNotificationSet =
        MutableStateFlow(sharedPreferences.getLong(NOTIFICATION_TIME_KEY, 0) > 0)
    val isNotificationSet
        get() = _isNotificationSet

    fun showError(message: String) {
        viewModelScope.launch {
            _errImage.value = getRandomErrorImage()
            _errMessage.value = message
            _errStatus.value = true
        }
    }

    fun resetErrorState() {
        viewModelScope.launch {
            _errImage.value = null
            _errMessage.value = null
            _errStatus.value = false
        }
    }

    fun hideSplash() {
        viewModelScope.launch {
            mShowSplash.value = false
        }
    }

    fun setBackgroundImage(ctx: Context) {
        val image = ctx.getRandomBgImage()
        viewModelScope.launch {
            _backgroundImage.value = image
        }
    }

    var sharePreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                ALARM_TIME_KEY -> {
                    val alarmTime = sharedPreferences.getLong(ALARM_TIME_KEY, 0)
                    _isAlarmSet.value = alarmTime > 0L
                }
                NOTIFICATION_TIME_KEY -> {
                    val notificationTime = sharedPreferences.getLong(NOTIFICATION_TIME_KEY, 0)
                    _isNotificationSet.value = notificationTime > 0L
                }
                NEXT_MAP_KEY -> {
                }
            }
        }

    private fun getRandomErrorImage(): Int {
        val errImages = listOf(
            R.drawable.er_bang,
            R.drawable.er_bloodhound,
            R.drawable.er_caustic,
            R.drawable.er_gibby,
            R.drawable.er_mirage,
            R.drawable.er_pathy,
            R.drawable.er_wraith
        )
        val rand = Random()
        return errImages[rand.nextInt(errImages.size)]
    }

    fun scheduleNotification(ctx: Context, isAlarm: Boolean, timeRemaining: Long) {
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val scheduleTime = System.currentTimeMillis() + timeRemaining
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(scheduleTime, getReceiver(ctx, isAlarm)),
            getReceiver(ctx, isAlarm)
        )
        with(sharedPreferences.edit()) {
            val target = if (isAlarm) ALARM_TIME_KEY else NOTIFICATION_TIME_KEY
            putLong(target, scheduleTime)
            commit()
        }
    }

    private fun getReceiver(ctx: Context, isAlarm: Boolean): PendingIntent {
        var flags = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_IMMUTABLE
        }
        flags += PendingIntent.FLAG_CANCEL_CURRENT
        val intent = Intent(ctx, AlarmBroadCastReceiver::class.java).apply {
            putExtra("ALARM", isAlarm)
        }
        val requestCode = if (isAlarm) RequestCodes.ALARM() else RequestCodes.NOTIFICATION()
        return PendingIntent.getBroadcast(
            ctx,
            requestCode,
            intent,
            flags
        )
    }

    fun cancelAlert(ctx: Context, isAlarm: Boolean) {
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(this.getReceiver(ctx, isAlarm))
        with(sharedPreferences.edit()) {
            when (isAlarm) {
                true -> {
                    putLong(ALARM_TIME_KEY, 0)
                }
                false -> {
                    putLong(NOTIFICATION_TIME_KEY, 0)
                }
            }
            commit()
        }
    }

    fun cancelAllAlerts(ctx: Context) {
        cancelAlert(ctx, isAlarm = true)
        cancelAlert(ctx, isAlarm = false)
    }

    //  Resets valid alarms, cancels invalid alarms
    fun resetAlerts(ctx: Context) {
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmTime = sharedPreferences.getLong(ALARM_TIME_KEY, 0)
        val notificationTime = sharedPreferences.getLong(NOTIFICATION_TIME_KEY, 0)
        if (alarmTime > 0 && alarmTime < System.currentTimeMillis()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, getReceiver(ctx, true))
        } else {
            with(sharedPreferences.edit()) {
                putLong(ALARM_TIME_KEY, 0)
                commit()
            }
            alarmManager.cancel(getReceiver(ctx, true))
        }
        if (notificationTime > 0 && notificationTime < System.currentTimeMillis()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, getReceiver(ctx, false))
        } else {
            with(sharedPreferences.edit()) {
                putLong(NOTIFICATION_TIME_KEY, 0)
                commit()
            }
            alarmManager.cancel(getReceiver(ctx, false))
        }
    }

    // Cancels alerts that should've already gone off
    fun verifyAlerts(ctx: Context) {
        val alarmTime = sharedPreferences.getLong(ALARM_TIME_KEY, 0)
        val notificationTime = sharedPreferences.getLong(NOTIFICATION_TIME_KEY, 0)
        val currentTime = System.currentTimeMillis()
        if (currentTime > alarmTime) {
            cancelAlert(ctx, true)
        }
        if (currentTime > notificationTime) {
            cancelAlert(ctx, false)
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharePreferencesListener)
    }

}