package com.example.apexmaprotations.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat

fun NotificationManagerCompat.buildChannel(name: String, description: String, channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            setDescription(description)
        }
        createNotificationChannel(channel)
    }
}