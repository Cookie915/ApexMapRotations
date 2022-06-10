package com.example.apexmaprotations.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import com.example.apexmaprotations.R
import com.example.apexmaprotations.activities.LockScreenActivity
import com.example.apexmaprotations.alarm.AlarmBroadCastReceiver
import com.example.apexmaprotations.viewmodels.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class RequestCodes {
    NOTIFICATION, ALARM;
    operator fun invoke(): Int {
        return ordinal
    }
}

fun Context.scheduleNotification(isAlarm: Boolean, timeRemaining: Long) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val scheduleTime = System.currentTimeMillis() + 500
    with(alarmManager) {
        setExact(AlarmManager.RTC_WAKEUP, scheduleTime, getReceiver(isAlarm))
    }
    CoroutineScope(Dispatchers.IO).launch {
        val dataStoreTarget = if (isAlarm) ALARM_TIME else NOTIFICATION_TIME
        dataStore.edit {
            it[dataStoreTarget] = scheduleTime
        }
    }
}

fun Context.cancelNotification(isAlarm: Boolean) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(getReceiver(isAlarm))
    CoroutineScope(Dispatchers.IO).launch {
        val dataStoreTarget = if (isAlarm) ALARM_TIME else NOTIFICATION_TIME
        dataStore.edit {
            it[dataStoreTarget] = 0
        }
    }
}

//  Verifies alarms are valid and resets them with alarm manager
suspend fun Context.resetAlarms() {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmTime = dataStore.data.first()[ALARM_TIME] ?: 0
    val notifyTime = dataStore.data.first()[NOTIFICATION_TIME] ?: 0
    if (alarmTime > 0 && alarmTime < System.currentTimeMillis()) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, getReceiver(true))
    } else {
        dataStore.edit {
            it[ALARM_TIME] = 0
        }
        alarmManager.cancel(getReceiver(true))
    }
    if (notifyTime > 0 && notifyTime < System.currentTimeMillis()) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifyTime, getReceiver(false))
    } else {
        dataStore.edit {
            it[NOTIFICATION_TIME] = 0
        }
        alarmManager.cancel(getReceiver(false))
    }
}

suspend fun Context.cancelAlarmsNotifications() {
    dataStore.edit {
        it[ALARM_TIME] = 0L
        it[NOTIFICATION_TIME] = 0L
    }
    cancelNotification(true)
    cancelNotification(false)
}

fun Context.getReceiver(isAlarm: Boolean): PendingIntent {
    var flags = 0
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flags = PendingIntent.FLAG_IMMUTABLE
    }
    flags += PendingIntent.FLAG_CANCEL_CURRENT
    val intent = Intent(this, AlarmBroadCastReceiver::class.java).apply {
        putExtra("ALARM", isAlarm)
    }
    val requestCode = if (isAlarm) RequestCodes.ALARM() else RequestCodes.NOTIFICATION()
    return PendingIntent.getBroadcast(
        this,
        requestCode,
        intent,
        flags
    )
}

fun Context.showNotificationWithFullScreenIntent(
    isAlarm: Boolean,
    isLockScreen: Boolean = false,
    title: String = "Apex Map Change",
    description: String = "Apex map changed to",
) {
    val channelId = if (isAlarm) "Alarms" else "Notifications"
    val icon = if (isAlarm) R.drawable.ic_notify_filled else R.drawable.ic_alert_3
    val requestCode = if (isAlarm) RequestCodes.ALARM() else RequestCodes.NOTIFICATION()
    val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(icon)
        .setColor(ContextCompat.getColor(this, R.color.apex_red_dark))
        .setContentTitle(title)
        .setContentText(description)
        .setAutoCancel(true)
        .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
        .setSound(sound)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setPriority(NotificationCompat.PRIORITY_MAX)
    if (isAlarm) {
        builder.setFullScreenIntent(getFullScreenIntent(isLockScreen), true)
    } else {
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }
        var flags = PendingIntent.FLAG_CANCEL_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags += PendingIntent.FLAG_IMMUTABLE
        }
        val pi = PendingIntent.getActivity(this, requestCode, intent, flags)
        builder.setContentIntent(pi)
    }
    with(NotificationManagerCompat.from(this)) {
        buildChannel(channelId, description, channelId)
        val notification = builder.build()
        notify(requestCode, notification)
    }
}

private fun Context.getFullScreenIntent(isLockScreen: Boolean): PendingIntent {
    val intent: Intent = if (isLockScreen) {
        Intent(this, LockScreenActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    } else {
        packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }!!
    }
    var flag = PendingIntent.FLAG_CANCEL_CURRENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
    }
    return PendingIntent.getActivity(this, RequestCodes.ALARM(), intent, flag)
}

//  Cancels alarms if time has already passed since map change
fun Context.verifyAlarms() {
    CoroutineScope(Dispatchers.IO).launch {
        val alarmTime = dataStore.data.first()[ALARM_TIME] ?: 0L
        val notifyTime = dataStore.data.first()[NOTIFICATION_TIME] ?: 0L
        val currentTime = System.currentTimeMillis()
        if (currentTime > alarmTime) {
            cancelNotification(true)
            dataStore.edit {
                it[ALARM_TIME] = 0L
            }
        }
        if (System.currentTimeMillis() > notifyTime) {
            cancelNotification(false)
            dataStore.edit {
                it[NOTIFICATION_TIME] = 0L
            }
        }
    }
}