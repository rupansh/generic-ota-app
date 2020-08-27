package com.rupanshkek.generic_ota.fetch_backends

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

interface Fetch {
    fun fetchData(device: String): RomDl?

    fun getLatest(deviceData: RomDl): Pair<LocalDate, LocalDate> {
        val deviceBuildDate =
            Instant.ofEpochSecond(android.os.Build.TIME / 1000).atZone(ZoneId.systemDefault()).toLocalDate()

        return Pair(deviceBuildDate, deviceData.buildDate)
    }
}