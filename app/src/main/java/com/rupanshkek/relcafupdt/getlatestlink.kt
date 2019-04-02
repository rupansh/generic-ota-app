package com.rupanshkek.relcafupdt

import org.jsoup.Jsoup


// fetches latest link from the provided thread
fun getLatestLink(url: String): String{
    val doc = Jsoup.connect(url).get()
    val links = doc.select("a[href]")
    var retval = "notfound"

    for(link in links){
        if(link.attr("href").startsWith("https://sourceforge.net")) {
            retval = link.attr("href")
        }
    }

    return retval
}

// fetches latest link for the detected device
fun getDeviceLink(): String{
    var threadlink = ""

    when(android.os.Build.DEVICE){
        "land" -> threadlink = "https://forum.xda-developers.com/xiaomi-redmi-3s/development/rom-reloaded-caf-t3891208"
        "beryllium" -> threadlink = "https://forum.xda-developers.com/poco-f1/development/rom-reloaded-caf-t3880429"
    }

    return getLatestLink(threadlink)
}