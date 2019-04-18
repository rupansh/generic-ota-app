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

package com.rupanshkek.generic_ota

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rupanshkek.generic_ota.NetworkingTasks.checkLatest
import kotlinx.coroutines.*


class UpdateNotificationJob : JobIntentService() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onHandleWork(intent: Intent) {
        createNotificationChannel()

        uiScope.launch {
            var checkLatestArr = listOf("")

            val backtask = async(Dispatchers.Default) {
                val devicesArr = resources.getStringArray(R.array.devicearr)
                var threadlink = ""

                for (device in devicesArr) {
                    val thrddevarr = device.split("|")
                    if (Build.DEVICE == thrddevarr[0]) {
                        threadlink = thrddevarr[1]
                    }
                }

                checkLatestArr = checkLatest(threadlink)
            }
            backtask.await()

            if (checkLatestArr[0].toInt() < checkLatestArr[1].toInt()) {
                // Create an explicit intent for an Activity in your app
                val ourIntent = Intent(this@UpdateNotificationJob, ScrollingActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent = PendingIntent.getActivity(this@UpdateNotificationJob, 0, ourIntent, 0)

                val updateBuilder = NotificationCompat.Builder(this@UpdateNotificationJob, "UPDATER_OTA")
                    .setSmallIcon(R.drawable.ic_download)
                    .setContentTitle("Update Available!")
                    .setContentText("Click to download")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                with(NotificationManagerCompat.from(this@UpdateNotificationJob)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(0, updateBuilder.build())
                }

            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = "UPDATER_NOTI"
        val descriptionText = "Notification Channel for fetching updates"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("UPDATER_OTA", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}