package com.example.apexmaprotations.alarm

import android.app.AlarmManager
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.example.apexmaprotations.util.ALARM_TIME_KEY
import com.example.apexmaprotations.util.NEXT_MAP_KEY
import com.example.apexmaprotations.util.NOTIFICATION_TIME_KEY
import com.example.apexmaprotations.util.showNotificationWithFullScreenIntent
import com.example.apexmaprotations.viewmodels.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmBroadCastReceiver @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val appViewModel: AppViewModel
) : BroadcastReceiver() {
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
                            appViewModel.resetAlerts(context)
                        } else {
                            appViewModel.cancelAllAlerts(context)
                        }
                    }
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                //  reset alarms after reboot
                CoroutineScope(Dispatchers.IO).launch {
                    if (Build.VERSION.SDK_INT >= 31) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            appViewModel.resetAlerts(context)
                        } else {
                            appViewModel.cancelAllAlerts(context)
                        }
                    } else {
                        appViewModel.resetAlerts(context)
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