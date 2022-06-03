package com.example.apexmaprotations.activities

import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.apexmaprotations.databinding.ActivityLockScreenBinding
import com.example.apexmaprotations.util.turnScreenOffAndKeyGuardOn
import com.example.apexmaprotations.util.turnScreenOnAndKeyguardOff


class LockScreenActivity : AppCompatActivity() {
    private val binding: ActivityLockScreenBinding by lazy {
        ActivityLockScreenBinding.inflate(layoutInflater)
    }
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("tester", "created")
        setContentView(binding.root)
        turnScreenOnAndKeyguardOff()
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI)
    }

    override fun onStart() {
        super.onStart()
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyGuardOn()
        mediaPlayer.stop()
    }

}

