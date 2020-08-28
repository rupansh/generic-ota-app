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
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class XDAFetch(private val prefix: String, private val devicesArr: Array<String>): Fetch {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d")!!
    private lateinit var threadDoc: Document

    // fetches latest link for the detected device
    private fun fetchDeviceLink(): String {
        val doc = threadDoc.select("div.postbit-content.postbit-content-moderated")
        val links = doc.select("a[href]")

        return links.single { it.attr("href").startsWith(prefix) }.attr("href")
    }

    // Fetches build date
    private fun fetchBuildDate(): LocalDate {
        val doc = threadDoc.select("div.postbit-content.postbit-content-moderated")
        val blist = doc.select("b")
        val latestBuild = blist.single { it.text() == "Last Updated" }.nextSibling().toString().split(" ")[1]

        return LocalDate.parse(latestBuild,  dateTimeFormatter)
    }

    // Finds Maintainer
    private fun fetchMaintainer(): String {
        return threadDoc.select("a.bigfusername.xda-popup-trigger").text().split(" ")[0]
    }

    // Fetches Rom Title from thread
    private fun fetchTitle(): String {
        val title = threadDoc.select("div[id=thread-header-bloglike]").select("h1").text()

        return title.split("]").single { !it.startsWith("[") && it.isNotBlank() }.split("[")[0].trim()
    }

    override fun fetchData(device: String): RomDl? {
        val deviceThread = devicesArr.singleOrNull { it.split("|")[0] == device }?.split("|")?.get(1)?: return null

        threadDoc = Jsoup.connect(deviceThread).get()
        return RomDl(device, fetchDeviceLink(), "ROM:  ${fetchTitle()}", fetchBuildDate(), fetchMaintainer(), deviceThread)
    }
}