package com.example.apexmaprotations.alarm

import android.app.AlarmManager
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.datastore.preferences.core.edit
import com.example.apexmaprotations.util.*
import com.example.apexmaprotations.viewmodels.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmBroadCastReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val alarmManager = getSystemService(
            context,
            AlarmManager::class.java
        ) as AlarmManager
        when (intent.action) {
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                if (Build.VERSION.SDK_INT >= 31) {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (alarmManager.canScheduleExactAlarms()) {
                            context.resetAlarms()
                        } else {
                            context.cancelAlarmsNotifications()
                        }
                    }
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                //  reset alarms after reboot
                CoroutineScope(Dispatchers.IO).launch {
                    if (Build.VERSION.SDK_INT >= 31) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            context.resetAlarms()
                        } else {
                            context.cancelAlarmsNotifications()
                        }
                    } else {
                        context.resetAlarms()
                    }
                }
            }
            else -> {
                // alarm actions
                CoroutineScope(Dispatchers.IO).launch {
                    context.dataStore.edit {
                        it[NOTIFICATION_TIME] = 0
                        it[ALARM_TIME] = 0
                    }
                }
                val isAlarm = intent.extras?.get("ALARM") as Boolean
                with(context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
                    context.showNotificationWithFullScreenIntent(isAlarm, isDeviceLocked)
                }
            }
        }
    }
}