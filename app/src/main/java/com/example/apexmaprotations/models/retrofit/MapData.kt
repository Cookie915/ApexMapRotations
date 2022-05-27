package com.example.apexmaprotations.models.retrofit

import com.google.gson.annotations.SerializedName

data class MapData(
    @SerializedName("current")
    val currentMap: CurrentMap,

    @SerializedName("next")
    val nextMap: NextMap
)


data class CurrentMap(
    val start: Long,
    val end: Long,
    val readableDate_start: String,
    val readableDate_end: String,
    val map: String,
    val code: String,
    val DurationInSecs: Int,
    val DurationInMinutes: Int,
    val asset: String,
    val remainingSecs: Int,
    val remainingMins: Int,
    val remainingTimer: String,
)

data class NextMap(
    val start: Int,
    val end: Int,
    val readableDate_start: String,
    val readableDate_end: String,
    val map: String,
    val code: String,
    val DurationInSecs: Int,
    val DurationInMinutes: Int,
)