package com.example.apexmaprotations.activities

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.apexmaprotations.databinding.ActivityLockScreenBinding
import com.example.apexmaprotations.repo.ApexRepo
import com.example.apexmaprotations.util.NEXT_MAP
import com.example.apexmaprotations.util.dataStore
import com.example.apexmaprotations.util.turnScreenOffAndKeyGuardOn
import com.example.apexmaprotations.util.turnScreenOnAndKeyguardOff
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LockScreenActivity @Inject constructor(
    private var apexRepo: ApexRepo
) : AppCompatActivity() {
    private val binding: ActivityLockScreenBinding by lazy {
        ActivityLockScreenBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val mapName = dataStore.data.first()[NEXT_MAP] ?: ""
            binding.mapName.text = mapName
            binding.image.setImageDrawable(getDrawable(apexRepo.getRandomBgImage()))
            setContentView(binding.root)
        }
        turnScreenOnAndKeyguardOff()
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyGuardOn()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}

