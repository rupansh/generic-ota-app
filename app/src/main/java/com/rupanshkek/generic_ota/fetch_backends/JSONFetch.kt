package com.rupanshkek.generic_ota.fetch_backends

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class JSONFetch(private val jsonDataLink: String): Fetch {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d")!!

    @JsonClass(generateAdapter = true)
    data class JRomDl(
        val device: String,
        val download: String,
        @Json(name = "zip_name") val zipName: String,
        @Json(name = "build_date") val buildDate: String,
        val maintainer: String,
        @Json(name = "xda_thread") val xdaThread: String
    )

    override fun fetchData(device: String): RomDl? {
        val fetchedJson = Jsoup.connect(jsonDataLink).header("Accept", "text/javascript").get().body().text()

        val moshi: Moshi = Moshi.Builder().build()
        val devicesType = newParameterizedType(List::class.java, JRomDl::class.java)
        val adapter: JsonAdapter<List<JRomDl>> = moshi.adapter(devicesType)

        val jsonData = adapter.fromJson(fetchedJson)
        val ourDev = jsonData.orEmpty().singleOrNull {
            it.device == device
        }?: return null

        return RomDl(ourDev.device, ourDev.download, ourDev.zipName, LocalDate.parse(ourDev.buildDate, dateTimeFormatter), ourDev.maintainer, ourDev.xdaThread)
    }
}