package com.example.apexmaprotations.repo

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