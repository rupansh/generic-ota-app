package com.rupanshkek.relcafupdt

import java.time.Instant
import java.time.ZoneId


// Checks if an update is available
fun checkLatest(): List<String> {
    // Spent 30 minutes figuring this out ffs
    val deviceBuildDate =
        Instant.ofEpochSecond(android.os.Build.TIME / 1000).atZone(ZoneId.systemDefault()).toLocalDateTime().toString().split(
            "T"
        )[0].replace("-", "")

    val buildLinkArr = getDeviceLink().split('/')
    val latestBuildDate = buildLinkArr[buildLinkArr.lastIndex - 1].split("-")[3]

    return listOf(deviceBuildDate, latestBuildDate)
}