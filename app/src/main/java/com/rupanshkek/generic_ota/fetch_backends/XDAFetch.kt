/* Copyright 2019 Rupansh Sekar

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package com.rupanshkek.generic_ota.fetch_backends

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class XDAFetch(private val prefix: String, private val devicesArr: Array<String>): Fetch {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d")!!
    private lateinit var threadDoc: Document

    // fetches latest link for the detected device
    private fun fetchDeviceLink(): String {
        val doc = threadDoc.select("div.postbit-content.postbit-content-moderated")
        val links = doc.select("a[href]")
        var dllink = ""

        for (link in links) {
            if (link.attr("href").startsWith(prefix)) {
                dllink = link.attr("href")
            }
        }

        return dllink
    }

    // Fetches build date
    private fun fetchBuildDate(): LocalDate {
        var latestBuildDate = ""
        val doc = threadDoc.select("div.postbit-content.postbit-content-moderated")

        val blist = doc.select("b")

        for (i in blist) {
            if (i.text() == "Last Updated"){
                latestBuildDate = i.nextSibling().toString().split(" ")[1]
            }
        }

        return LocalDate.parse(latestBuildDate, dateTimeFormatter)
    }

    // Finds Maintainer
    private fun fetchMaintainer(): String {
        return threadDoc.select("a.bigfusername.xda-popup-trigger").text().split(" ")[0]
    }

    // Fetches Rom Title from thread
    private fun fetchTitle(): String {
        val title = threadDoc.select("div[id=thread-header-bloglike]").select("h1").text()
        var romtitle = ""

        for (i in title.split("]")){
            if (!i.startsWith("[") && i.isNotBlank()){
                romtitle = i.split("[")[0]
                if (romtitle.startsWith(" ")){
                    romtitle = romtitle.trim()
                }
            }
        }

        return romtitle
    }

    // reqData is dl link prefix
    override fun fetchData(device: String): RomDl? {
        for (xdev in devicesArr) {
            val thrddevarr = xdev.split("|")
            if (thrddevarr[0] == device) {
                threadDoc = Jsoup.connect(thrddevarr[1]).get()
                return RomDl(device, fetchDeviceLink(), "ROM:  ${fetchTitle()}", fetchBuildDate(), fetchMaintainer(), thrddevarr[1])
            }
        }

        return null
    }
}