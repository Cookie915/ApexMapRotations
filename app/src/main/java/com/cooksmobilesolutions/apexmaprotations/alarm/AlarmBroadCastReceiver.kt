package com.cooksmobilesolutions.apexmaprotations.alarm

import android.app.AlarmManager
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.cooksmobilesolutions.apexmaprotations.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmBroadCastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

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
                            context.resetAlerts(sharedPreferences)
                        } else {
                            context.cancelAllAlerts(sharedPreferences)
                        }
                    }
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                //  reset alarms after reboot
                CoroutineScope(Dispatchers.IO).launch {
                    if (Build.VERSION.SDK_INT >= 31) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            context.resetAlerts(sharedPreferences)
                        } else {
                            context.cancelAllAlerts(sharedPreferences)
                        }
                    } else {
                        context.resetAlerts(sharedPreferences)
                    }
                }
            }
            else -> {
                with(sharedPreferences.edit()) {
                    putLong(NOTIFICATION_TIME_KEY, 0)
                    putLong(ALARM_TIME_KEY, 0)
                    commit()
                }
                val nextMap = sharedPreferences.getString(NEXT_MAP_KEY, "")
                val isAlarm = intent.extras?.get("ALARM") as Boolean
                with(context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
                    context.showNotificationWithFullScreenIntent(
                        isAlarm,
                        isDeviceLocked,
                        "Apex Map Change",
                        "Battle Royal map changed to $nextMap"
                    )
                }
            }
        }
    }
}