package com.example.apexmaprotations.util

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.example.apexmaprotations.viewmodels.BattleRoyalViewModel


fun formatTime(minutes: Long, seconds: Long): List<String> {
    val result: MutableList<String> = mutableListOf()
    if (minutes < 10) {
        val formattedMinute = "0$minutes"
        result.add(formattedMinute)
    } else {
        result.add(minutes.toString())
    }
    if (seconds < 10){
        val formattedSeconds = "0$seconds"
        result.add(formattedSeconds)
    } else {
        result.add(seconds.toString())
    }
    return result
}

fun assignMapImage(
    map: String,
    view: ImageView,
    battleRoyalViewModel: BattleRoyalViewModel,
    ctx: Context
) {
    when (map) {
        "Storm Point" -> {
            val mapImg = battleRoyalViewModel.getStormPointImg()
            view.setImageDrawable(AppCompatResources.getDrawable(ctx, mapImg))
        }
        "King's Canyon" -> {
            val mapImg = battleRoyalViewModel.getStormPointImg()
            view.setImageDrawable(AppCompatResources.getDrawable(ctx, mapImg))
        }
        "Olympus" -> {
            val mapImg = battleRoyalViewModel.getStormPointImg()
            view.setImageDrawable(AppCompatResources.getDrawable(ctx, mapImg))
        }
        "World's Edge" -> {
            val mapImg = battleRoyalViewModel.getWorldsEdgeImg()
            Glide.with(ctx)
                .load(mapImg)
                .centerCrop()
                .into(view)
        }
    }
}