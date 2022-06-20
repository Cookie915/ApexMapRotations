package com.example.apexmaprotations.activities

import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.apexmaprotations.databinding.ActivityLockScreenBinding
import com.example.apexmaprotations.repo.ApexRepo
import com.example.apexmaprotations.util.NEXT_MAP
import com.example.apexmaprotations.util.assignMapImage
import com.example.apexmaprotations.util.turnScreenOffAndKeyGuardOn
import com.example.apexmaprotations.util.turnScreenOnAndKeyguardOff
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel
import com.example.apexmaprotations.viewmodels.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LockScreenActivity : AppCompatActivity() {
    private val binding: ActivityLockScreenBinding by lazy {
        ActivityLockScreenBinding.inflate(layoutInflater)
    }
    private val apexViewModel by viewModels<BattleRoyalViewModel>()

    @Inject
    lateinit var apexRepo: ApexRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val image = binding.image
        lifecycleScope.launch {
            //  todo add default imaage when NEXT_MAP is null
            val mapName = dataStore.data.first()[NEXT_MAP] ?: ""
            assignMapImage(mapName, image, apexRepo, this@LockScreenActivity)
            binding.mapName.text = mapName
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

