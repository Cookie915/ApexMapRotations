package com.example.apexmaprotations.util

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.apexmaprotations.R
import com.example.apexmaprotations.activities.LockScreenActivity
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
        .setDefaults(Notification.DEFAULT_VIBRATE)
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
