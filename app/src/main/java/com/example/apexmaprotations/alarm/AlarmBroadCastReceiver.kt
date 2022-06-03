package com.example.apexmaprotations.alarm

import android.app.AlarmManager
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.example.apexmaprotations.util.showNotificationWithFullScreenIntent

const val LOCK_SCREEN_KEY = "lockScreenKey"

class AlarmBroadCastReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {

            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                if (Build.VERSION.SDK_INT >= 31) {
                    val alarmManager = getSystemService(
                        context,
                        AlarmManager::class.java
                    ) as AlarmManager
                    if (alarmManager.canScheduleExactAlarms()) {
                    }
                    //  reschedule alarms
                }
            }

            Intent.ACTION_BOOT_COMPLETED -> {
                //  reset alarms after reboot
            }

            else -> {
                // alarm actions
                Log.i("tester", "got broadcast")
                val isAlarm = intent.extras?.get("ALARM") as Boolean
                with(context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
                    context.showNotificationWithFullScreenIntent(isAlarm, isDeviceLocked)
                }
            }
        }
    }
}