package com.example.apexmaprotations.repo

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.apexmaprotations.viewmodels.ApexViewModel

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

fun assignMapImage(map: String, view: ImageView, apexViewModel: ApexViewModel, ctx: Context){
    when(map){
        "Storm Point" ->{
            val mapImg = apexViewModel.getStormPointImg()
            Glide.with(ctx)
                .load(mapImg)
                .centerCrop()
                .into(view)
        }
        "King's Canyon" -> {
            val mapImg = apexViewModel.getKingCanyonImg()
            Glide.with(ctx)
                .load(mapImg)
                .centerCrop()
                .into(view)
        }
        "Olympus" -> {
            val mapImg = apexViewModel.getOlympusImg()
            Glide.with(ctx)
                .load(mapImg)
                .centerCrop()
                .into(view)
        }
        "World's Edge" -> {
            val mapImg = apexViewModel.getWorldsEdgeImg()
            Glide.with(ctx)
                .load(mapImg)
                .centerCrop()
                .into(view)
        }
    }
}