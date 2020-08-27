package com.rupanshkek.generic_ota.fetch_backends

import com.squareup.moshi.*
import org.jsoup.Jsoup
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.JsonAdapter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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

        for (it in jsonData.orEmpty()) {
            if (it.device == device) {
                return RomDl(it.device, it.download, it.zipName, LocalDate.parse(it.buildDate, dateTimeFormatter), it.maintainer, it.xdaThread)
            }
        }

        return null
    }
}