package com.rupanshkek.generic_ota

import com.squareup.moshi.*
import org.jsoup.Jsoup
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.JsonAdapter
import java.time.Instant
import java.time.ZoneId


object JSONFetch {
    var jsonData: List<RomDl>? = null
    var ourIndex: Int = 0

    @JsonClass(generateAdapter = true)
    data class RomDl(
        val device: String,
        val download: String,
        @Json(name = "zip_name") val zipName: String,
        @Json(name = "build_date") val buildDate: String,
        val maintainer: String,
        @Json(name = "xda_thread") val xdaThread: String
    )

    fun fetchJson(link: String){
        jsonData = null

        val fetchedJson = Jsoup.connect(link).header("Accept", "text/javascript").get().body().text()

        val moshi: Moshi = Moshi.Builder().build()
        val devicesType = newParameterizedType(List::class.java, RomDl::class.java)
        val adapter: JsonAdapter<List<RomDl>> = moshi.adapter(devicesType)

        jsonData = adapter.fromJson(fetchedJson)
    }

    fun checkLatest(): List<String>{
        val deviceBuildDate =
            Instant.ofEpochSecond(android.os.Build.TIME / 1000).atZone(ZoneId.systemDefault()).toLocalDateTime().toString().split(
                "T"
            )[0].replace("-", "")

        return listOf(deviceBuildDate, jsonData!![ourIndex].buildDate.replace("-", ""))
    }
}