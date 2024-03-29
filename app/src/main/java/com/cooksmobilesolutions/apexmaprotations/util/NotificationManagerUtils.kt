package com.cooksmobilesolutions.apexmaprotations.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat


fun NotificationManagerCompat.buildChannel(name: String, description: String, channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        if (channelId == RequestCodes.ALARM.name) {
            val channel = NotificationChannel(channelId, name, importance).apply {
                enableLights(true)
                lightColor = Color.RED
                setDescription(description)
                enableVibration(true)
                val attr = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), attr)
                this.importance = importance
            }
            createNotificationChannel(channel)
        }
        if (channelId == RequestCodes.NOTIFICATION.name) {
            val channel = NotificationChannel(channelId, name, importance).apply {
                setDescription(description)
                enableLights(true)
                enableVibration(true)
                val attr = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .build()
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), attr)
            }
            createNotificationChannel(channel)
        }
    }
}