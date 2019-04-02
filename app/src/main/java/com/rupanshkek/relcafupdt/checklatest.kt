package com.rupanshkek.relcafupdt

import android.util.Log
import java.time.*
import com.parse.ParseObject
import com.parse.GetCallback
import com.parse.ParseException
import com.parse.ParseQuery
import java.time.format.DateTimeFormatter


object CheckingUpdates {

    var testingInfo: List<String?> = listOf("not avail")
    @Volatile var isTestingSet = false

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


    fun checkTesting() {
        val query = ParseQuery.getQuery<ParseObject>("gdrivebeta")

        var objectId = ""

        when (android.os.Build.DEVICE) {
            "land" -> objectId = "LwJSnpLIF1"
            "beryllium" -> objectId = "XVpDX770g5"
        }

        isTestingSet = false

        // The query will search for a ParseObject, given its objectId.
        // When the query finishes running, it will invoke the GetCallback
        // with either the object, or the exception thrown
        query.getInBackground(objectId, object : GetCallback<ParseObject> {
            override fun done(result: ParseObject, e: ParseException?) {
                if (e == null) {
                    if (result.getString("gdrivelink") != "") {
                        val testupdated = result.updatedAt.toString().split(" ")
                        val parsedupdated = LocalDate.parse(
                            testupdated[2] + " " + testupdated[1] + " " + testupdated[5],
                            DateTimeFormatter.ofPattern("dd MMM yyyy")
                        )
                        testingInfo = listOf(result.getString("gdrivelink"), parsedupdated.toString())
                    }else{
                        testingInfo = listOf("not avail")
                    }
                } else {
                    Log.e("RELCAF-OTA", "Testing get failed")
                }
                isTestingSet = true
            }
        })
    }
}