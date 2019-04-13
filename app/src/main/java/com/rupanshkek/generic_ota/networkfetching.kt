package com.rupanshkek.generic_ota

import org.jsoup.Jsoup
import java.time.Instant
import java.time.ZoneId


object NetworkingTasks {
    // fetches latest link for the detected device
    fun getDeviceLink(threadlink: String, prefix: String): String {
        val doc = Jsoup.connect(threadlink).get().select("div.postbit-content.postbit-content-moderated")
        val links = doc.select("a[href]")
        var dllink = ""

        for (link in links) {
            if (link.attr("href").startsWith(prefix)) {
                dllink = link.attr("href")
            }
        }

        return dllink
    }

    // Checks if an update is available
    fun checkLatest(threadlink: String): List<String> {
        var latestBuildDate = ""

        // Spent 30 minutes figuring this out ffs
        val deviceBuildDate =
            Instant.ofEpochSecond(android.os.Build.TIME / 1000).atZone(ZoneId.systemDefault()).toLocalDateTime().toString().split(
                "T"
            )[0].replace("-", "")

        val doc = Jsoup.connect(threadlink).get().select("div.postbit-content.postbit-content-moderated")

        val blist = doc.select("b")

        for (i in blist) {
            if (i.text() == "Last Updated"){
                latestBuildDate = i.nextSibling().toString().split(" ")[1].replace("-", "")
            }
        }

        return listOf(deviceBuildDate, latestBuildDate)
    }

    // Finds Maintainer
    fun fetchMaintainer(threadlink:String): String{
        val doc = Jsoup.connect(threadlink).get()

        return doc.select("a.bigfusername.xda-popup-trigger").text().split(" ")[0]
    }

    // Fetches Rom Title from thread
    fun fetchTitle(threadlink: String): String{
        val doc = Jsoup.connect(threadlink).get()
        val title = doc.select("div[id=thread-header-bloglike]").select("h1").text()
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
}