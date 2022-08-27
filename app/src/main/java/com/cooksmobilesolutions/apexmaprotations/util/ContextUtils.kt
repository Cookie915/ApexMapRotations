package com.cooksmobilesolutions.apexmaprotations.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.cooksmobilesolutions.apexmaprotations.R
import com.cooksmobilesolutions.apexmaprotations.activities.LockScreenActivity
import com.cooksmobilesolutions.apexmaprotations.alarm.AlarmBroadCastReceiver
import java.util.*
import kotlin.math.sqrt


enum class RequestCodes {
    NOTIFICATION, ALARM;
    operator fun invoke(): Int {
        return ordinal
    }
}

fun Context.showNotificationWithFullScreenIntent(
    isAlarm: Boolean,
    isLockScreen: Boolean = false,
    title: String = "Apex Map Change",
    description: String = "Apex map changed to",
) {
    val channelId = if (isAlarm) RequestCodes.ALARM.name else RequestCodes.NOTIFICATION.name
    val icon = if (isAlarm) R.drawable.ic_alarm else R.drawable.ic_notification
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

fun Context.getReceiver(isAlarm: Boolean): PendingIntent? {
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

fun Context.cancelAlert(isAlarm: Boolean, sharedPreferences: SharedPreferences) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(this.getReceiver(isAlarm))
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

fun Context.cancelAllAlerts(sharedPreferences: SharedPreferences) {
    cancelAlert(true, sharedPreferences)
    cancelAlert(false, sharedPreferences)
}

//  Resets valid alarms, cancels invalid alarms
fun Context.resetAlerts(sharedPreferences: SharedPreferences) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmTime = sharedPreferences.getLong(ALARM_TIME_KEY, 0)
    val notificationTime = sharedPreferences.getLong(NOTIFICATION_TIME_KEY, 0)
    if (alarmTime > 0 && alarmTime < System.currentTimeMillis()) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, getReceiver(true))
    } else {
        with(sharedPreferences.edit()) {
            putLong(ALARM_TIME_KEY, 0)
            commit()
        }
        alarmManager.cancel(getReceiver(true))
    }
    if (notificationTime > 0 && notificationTime < System.currentTimeMillis()) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, getReceiver(false))
    } else {
        with(sharedPreferences.edit()) {
            putLong(NOTIFICATION_TIME_KEY, 0)
            commit()
        }
        alarmManager.cancel(getReceiver(false))
    }
}


fun Context.getRandomBgImage(): Int {
    Log.i("tester14", "ctx get image")
    val bgImages: List<Int>
    val metrics = resources.displayMetrics
    val yInches = metrics.heightPixels / metrics.ydpi
    val xInches = metrics.widthPixels / metrics.xdpi
    val diagonalInches: Double = sqrt(xInches * xInches + yInches * yInches).toDouble()
    val isTablet = diagonalInches > 6.5
    if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE || isTablet) {
        bgImages = listOf(
            R.drawable.bg_valk_tablet,
            R.drawable.bg_wattson_tablet,
            R.drawable.bg_octone_tablet,
            R.drawable.bg_crypto_tablet
        )
    } else {
        bgImages = listOf(
            R.drawable.bg_ash,
            R.drawable.bg_bloodhound,
            R.drawable.bg_octane,
            R.drawable.bg_valk,
            R.drawable.bg_pathy
        )
    }
    val rand = Random()
    val image = bgImages[rand.nextInt(bgImages.size)]
    Log.i("tester14", image.toString())
    return image
}
