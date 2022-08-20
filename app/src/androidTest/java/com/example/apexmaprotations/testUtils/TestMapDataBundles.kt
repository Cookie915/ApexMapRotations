package com.example.apexmaprotations.testUtils

import com.example.apexmaprotations.data.retrofit.MapDataBundle

object TestMapDataBundles {
    val SuccessfulMapDataBundle = MapDataBundle(
        arenas = MapDataBundle.Arenas(
            current = MapDataBundle.Arenas.Current(
                start = 1657009800,
                end = 1657010700,
                asset = "https://apexlegendsstatus.com/assets/maps/Arenas_Dropoff.png",
                code = "arenas_composite",
                durationInMinutes = 15,
                durationInSecs = 900,
                map = "Drop Off",
                readableDateEnd = "2022-07-05 08:45:00",
                readableDateStart = "2022-07-05 08:30:00",
                remainingMins = 4,
                remainingSecs = 214,
                remainingTimer = "00:03:34"
            ),
            next = MapDataBundle.Arenas.Next(
                asset = "https://apexlegendsstatus.com/assets/maps/Arena_Habitat.png",
                code = "arenas_habitat",
                durationInMinutes = 15,
                durationInSecs = 900,
                end = 1657011600,
                map = "Habitat",
                readableDateEnd = "2022-07-05 09:00:00",
                readableDateStart = "2022-07-05 08:45:00",
                start = 1657010700
            )
        ),
        arenasRanked = MapDataBundle.ArenasRanked(
            current = MapDataBundle.ArenasRanked.Current(
                asset = "https://apexlegendsstatus.com/assets/maps/Arena_Habitat.png",
                code = "arenas_habitat",
                durationInSecs = 2400,
                durationInMinutes = 40,
                end = 1657011600,
                map = "Habitat",
                readableDateEnd = "2022-07-05 09:00:00",
                readableDateStart = "2022-07-05 08:20:00",
                remainingMins = 19,
                remainingSecs = 1114,
                remainingTimer = "00:18:34",
                start = 1657009200
            ),
            next = MapDataBundle.ArenasRanked.Next(
                asset = "https://apexlegendsstatus.com/assets/maps/Arena_Encore.png",
                code = "arenas_encore",
                durationInMinutes = 40,
                durationInSecs = 2400,
                end = 1657014000,
                map = "Encore",
                readableDateEnd = "2022-07-05 09:40:00",
                readableDateStart = "2022-07-05 09:00:00",
                start = 1657011600
            ),
        ),
        battleRoyale = MapDataBundle.BattleRoyale(
            current = MapDataBundle.BattleRoyale.Current(
                start = 1657008000,
                end = 1657011600,
                asset = "https://apexlegendsstatus.com/assets/maps/Storm_Point.png",
                code = "storm_point_rotation",
                durationInMinutes = 60,
                durationInSecs = 3600,
                map = "Storm Point",
                readableDateEnd = "2022-07-05 09:00:00",
                readableDateStart = "2022-07-05 08:00:00",
                remainingMins = 19,
                remainingSecs = 1114,
                remainingTimer = "00:18:34"
            ),
            next = MapDataBundle.BattleRoyale.Next(
                asset = "https://apexlegendsstatus.com/assets/maps/Olympus.png",
                code = "olympus_rotation",
                durationInMinutes = 60,
                durationInSecs = 3600,
                end = 1657015200,
                map = "Olympus",
                readableDateEnd = "2022-07-05 10:00:00",
                readableDateStart = "2022-07-05 09:00:00",
                start = 1657011600
            )
        ),
        ranked = MapDataBundle.Ranked(
            current = MapDataBundle.Ranked.Current(
                asset = "https://apexlegendsstatus.com/assets/maps/Worlds_Edge.png",
                code = "worlds_edge_rotation",
                durationInSecs = 3542400,
                durationInMinutes = 59040,
                end = 1659978000,
                map = "World's Edge",
                readableDateEnd = "2022-08-08 17:00:00",
                readableDateStart = "2022-06-28 17:00:00",
                remainingMins = 49459,
                remainingSecs = 2967514,
                remainingTimer = "824:18:34",
                start = 1656435600
            ),
            next = MapDataBundle.Ranked.Next(
                asset = "https://apexlegendsstatus.com/assets/maps/Unknown.png",
                code = "unknown_rotation",
                durationInMinutes = -27666300,
                durationInSecs = -1659978000,
                end = 0,
                map = "Unknown",
                readableDateEnd = "1970-01-01 00:00:00",
                readableDateStart = "2022-08-08 17:00:00",
                start = 1659978000
            )
        )
    )
}