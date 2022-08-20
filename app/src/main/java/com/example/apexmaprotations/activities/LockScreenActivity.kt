package com.example.apexmaprotations.activities

import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.apexmaprotations.databinding.ActivityLockScreenBinding
import com.example.apexmaprotations.repo.ApexRepoImpl
import com.example.apexmaprotations.util.NEXT_MAP_KEY
import com.example.apexmaprotations.util.getRandomBgImage
import com.example.apexmaprotations.util.turnScreenOffAndKeyGuardOn
import com.example.apexmaprotations.util.turnScreenOnAndKeyguardOff
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LockScreenActivity @Inject constructor(
    private var apexRepo: ApexRepoImpl,
    private var sharedPreferences: SharedPreferences
) : AppCompatActivity() {
    private val binding: ActivityLockScreenBinding by lazy {
        ActivityLockScreenBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mapName = sharedPreferences.getString(NEXT_MAP_KEY, "")
        val mapImage = getDrawable(this.getRandomBgImage())
        binding.mapName.text = mapName
        binding.image.setImageDrawable(mapImage)
        turnScreenOnAndKeyguardOff()
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyGuardOn()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}

