package com.cooksmobilesolutions.apexmaprotations.activities

import android.app.NotificationManager
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.cooksmobilesolutions.apexmaprotations.databinding.ActivityLockScreenBinding
import com.cooksmobilesolutions.apexmaprotations.repo.ApexRepoImpl
import com.cooksmobilesolutions.apexmaprotations.util.NEXT_MAP_KEY
import com.cooksmobilesolutions.apexmaprotations.util.getRandomBgImage
import com.cooksmobilesolutions.apexmaprotations.util.turnScreenOffAndKeyGuardOn
import com.cooksmobilesolutions.apexmaprotations.util.turnScreenOnAndKeyguardOff
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LockScreenActivity : AppCompatActivity() {
    @Inject
    lateinit var apexRepo: ApexRepoImpl

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    lateinit var mediaPlayer: MediaPlayer

    private val binding: ActivityLockScreenBinding by lazy {
        ActivityLockScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mediaPlayer = MediaPlayer()
        val ringTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        MediaPlayer.create(this, ringTone)
        mediaPlayer.start()

        Log.i("tester20", "lockscreen")
        val mapName = sharedPreferences.getString(NEXT_MAP_KEY, "")
        val mapImage = AppCompatResources.getDrawable(this, this.getRandomBgImage())
        binding.mapName.text = mapName
        binding.image.setImageDrawable(mapImage)
        turnScreenOnAndKeyguardOff()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        turnScreenOffAndKeyGuardOn()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}

