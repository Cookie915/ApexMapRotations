package com.example.apexmaprotations.models.retrofit

import com.google.gson.annotations.SerializedName

//data class MapData(
//    @SerializedName("current")
//    val currentMap: AllMapData.CurrentMap,
//
//    @SerializedName("next")
//    val nextMap: NextMap
//
//
//)

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

data class MapDataBundle(
    @SerializedName("arenas")
    val arenas: Arenas,
    @SerializedName("arenasRanked")
    val arenasRanked: ArenasRanked,
    @SerializedName("battle_royale")
    val battleRoyale: BattleRoyale,
    @Transient
    @SerializedName("ranked")
    val ranked: Ranked? = null
) {
    data class Arenas(
        @SerializedName("current")
        val current: Current,
        @SerializedName("next")
        val next: Next
    ) {
        data class Current(
            @SerializedName("asset")
            val asset: String,
            @SerializedName("code")
            val code: String,
            @SerializedName("DurationInMinutes")
            val durationInMinutes: Int,
            @SerializedName("DurationInSecs")
            val durationInSecs: Int,
            @SerializedName("end")
            val end: Long,
            @SerializedName("map")
            val map: String,
            @SerializedName("readableDate_end")
            val readableDateEnd: String,
            @SerializedName("readableDate_start")
            val readableDateStart: String,
            @SerializedName("remainingMins")
            val remainingMins: Int,
            @SerializedName("remainingSecs")
            val remainingSecs: Int,
            @SerializedName("remainingTimer")
            val remainingTimer: String,
            @SerializedName("start")
            val start: Long
        )

        data class Next(
            @SerializedName("asset")
            val asset: String,
            @SerializedName("code")
            val code: String,
            @SerializedName("DurationInMinutes")
            val durationInMinutes: Int,
            @SerializedName("DurationInSecs")
            val durationInSecs: Int,
            @SerializedName("end")
            val end: Long,
            @SerializedName("map")
            val map: String,
            @SerializedName("readableDate_end")
            val readableDateEnd: String,
            @SerializedName("readableDate_start")
            val readableDateStart: String,
            @SerializedName("start")
            val start: Long
        )
    }

    data class ArenasRanked(
        @SerializedName("current")
        val current: Current,
        @SerializedName("next")
        val next: Next
    ) {
        data class Current(
            @SerializedName("asset")
            val asset: String,
            @SerializedName("code")
            val code: String,
            @SerializedName("DurationInMinutes")
            val durationInMinutes: Int,
            @SerializedName("DurationInSecs")
            val durationInSecs: Int,
            @SerializedName("end")
            val end: Long,
            @SerializedName("map")
            val map: String,
            @SerializedName("readableDate_end")
            val readableDateEnd: String,
            @SerializedName("readableDate_start")
            val readableDateStart: String,
            @SerializedName("remainingMins")
            val remainingMins: Int,
            @SerializedName("remainingSecs")
            val remainingSecs: Int,
            @SerializedName("remainingTimer")
            val remainingTimer: String,
            @SerializedName("start")
            val start: Long
        )

        data class Next(
            @SerializedName("asset")
            val asset: String,
            @SerializedName("code")
            val code: String,
            @SerializedName("DurationInMinutes")
            val durationInMinutes: Int,
            @SerializedName("DurationInSecs")
            val durationInSecs: Int,
            @SerializedName("end")
            val end: Long,
            @SerializedName("map")
            val map: String,
            @SerializedName("readableDate_end")
            val readableDateEnd: String,
            @SerializedName("readableDate_start")
            val readableDateStart: String,
            @SerializedName("start")
            val start: Long
        )
    }

    data class BattleRoyale(
        @SerializedName("current")
        val current: Current,
        @SerializedName("next")
        val next: Next
    ) {
        data class Current(
            @SerializedName("asset")
            val asset: String,
            @SerializedName("code")
            val code: String,
            @SerializedName("DurationInMinutes")
            val durationInMinutes: Int,
            @SerializedName("DurationInSecs")
            val durationInSecs: Int,
            @SerializedName("end")
            val end: Long,
            @SerializedName("map")
            val map: String,
            @SerializedName("readableDate_end")
            val readableDateEnd: String,
            @SerializedName("readableDate_start")
            val readableDateStart: String,
            @SerializedName("remainingMins")
            val remainingMins: Int,
            @SerializedName("remainingSecs")
            val remainingSecs: Int,
            @SerializedName("remainingTimer")
            val remainingTimer: String,
            @SerializedName("start")
            val start: Long
        )

        data class Next(
            @SerializedName("asset")
            val asset: String,
            @SerializedName("code")
            val code: String,
            @SerializedName("DurationInMinutes")
            val durationInMinutes: Int,
            @SerializedName("DurationInSecs")
            val durationInSecs: Int,
            @SerializedName("end")
            val end: Long,
            @SerializedName("map")
            val map: String,
            @SerializedName("readableDate_end")
            val readableDateEnd: String,
            @SerializedName("readableDate_start")
            val readableDateStart: String,
            @SerializedName("start")
            val start: Long
        )
    }

    data class Ranked(
        @SerializedName("current")
        val current: Current,
        @SerializedName("next")
        val next: Next
    ) {
        data class Current(
            @SerializedName("asset")
            val asset: String,
            @SerializedName("code")
            val code: String,
            @SerializedName("DurationInMinutes")
            val durationInMinutes: Int,
            @SerializedName("DurationInSecs")
            val durationInSecs: Int,
            @SerializedName("end")
            val end: Long,
            @SerializedName("map")
            val map: String,
            @SerializedName("readableDate_end")
            val readableDateEnd: String,
            @SerializedName("readableDate_start")
            val readableDateStart: String,
            @SerializedName("remainingMins")
            val remainingMins: Int,
            @SerializedName("remainingSecs")
            val remainingSecs: Int,
            @SerializedName("remainingTimer")
            val remainingTimer: String,
            @SerializedName("start")
            val start: Long
        )

        data class Next(
            @SerializedName("asset")
            val asset: String,
            @SerializedName("code")
            val code: String,
            @SerializedName("DurationInMinutes")
            val durationInMinutes: Int,
            @SerializedName("DurationInSecs")
            val durationInSecs: Int,
            @SerializedName("end")
            val end: Long,
            @SerializedName("map")
            val map: String,
            @SerializedName("readableDate_end")
            val readableDateEnd: String,
            @SerializedName("readableDate_start")
            val readableDateStart: String,
            @SerializedName("start")
            val start: Long
        )
    }
}
