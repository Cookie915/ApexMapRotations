package com.example.apexmaprotations.util

import android.app.AlarmManager
import android.app.Notification
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
import com.example.apexmaprotations.repo.ALARM_TIME
import com.example.apexmaprotations.repo.NOTIFICATION_TIME
import com.example.apexmaprotations.viewmodels.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
            it[dataStoreTarget] = scheduleTime.toInt()
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

private fun Context.getReceiver(isAlarm: Boolean): PendingIntent {
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


private const val CHANNEL_ID = "Alarms & Notifications"
fun Context.showNotificationWithFullScreenIntent(
    isAlarm: Boolean,
    isLockScreen: Boolean = false,
    channelId: String = CHANNEL_ID,
    title: String = "Apex Map Change",
    description: String = "Apex map changed to",
) {
    val icon = if (isAlarm) R.drawable.ic_notify_filled else R.drawable.ic_alert_3
    val requestCode = if (isAlarm) RequestCodes.ALARM() else RequestCodes.NOTIFICATION()
    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(icon)
        .setColor(ContextCompat.getColor(this, R.color.apex_red_dark))
        .setContentTitle(title)
        .setContentText(description)
        .setAutoCancel(true)
        .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
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
        if (isAlarm) {
            notification.flags = Notification.FLAG_INSISTENT
        }
        notify(requestCode, notification)
    }
}

private fun Context.getFullScreenIntent(isLockScreen: Boolean): PendingIntent {
    val destination = LockScreenActivity::class.java
    val intent = Intent(this, destination)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK)
    var flag = PendingIntent.FLAG_CANCEL_CURRENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flag = PendingIntent.FLAG_IMMUTABLE
    }
    return PendingIntent.getActivity(this, RequestCodes.ALARM(), intent, flag)
}