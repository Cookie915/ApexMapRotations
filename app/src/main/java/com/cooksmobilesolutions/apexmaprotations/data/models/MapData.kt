package com.cooksmobilesolutions.apexmaprotations.data.models

data class MapData(
    val brCurrentMapName: String = "",
    val brNextMapName: String = "",
    val brEndTimeSecs: Long = 0,
    val arenasCurrentMapName: String = "",
    val arenasNextMapName: String = "",
    val arenasEndTimeSecs: Long = 0,
    val rankedArenasCurrentMapName: String = "",
    val rankedArenasNextMapName: String = "",
    val rankedArenasEndTimeSecs: Long = 0
)

