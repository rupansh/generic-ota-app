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

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.alespero.expandablecardview.ExpandableCardView
import com.rupanshkek.generic_ota.NetworkingTasks.checkLatest
import com.rupanshkek.generic_ota.NetworkingTasks.fetchMaintainer
import com.rupanshkek.generic_ota.NetworkingTasks.fetchTitle
import com.rupanshkek.generic_ota.NetworkingTasks.getDeviceLink
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.info_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


class ScrollingActivity : AppCompatActivity() {

    private var networkAvail = false
    private var threadlink = ""
    private var latestLink = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        fab.setOnClickListener {
            checkNetwork()

            if (networkAvail) {
                val rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate)
                fab.startAnimation(rotateAnim)

                getLink()
                updateReq()
            } else{
                MaterialDialog(this@ScrollingActivity).show {
                    icon(R.drawable.ic_no_wifi)
                    title(text = "No Internet Access")
                    message(text = "Turn on Cellular Data/WiFi to fetch updates!")
                    negativeButton(text = "Quit") { finish(); moveTaskToBack(true) }
                    onDismiss { finish(); moveTaskToBack(true) }
                }
                val latestzipcard = findViewById<ExpandableCardView>(R.id.latzip)
                val textView = findViewById<Button>(R.id.lat_button)
                val romincard = findViewById<ExpandableCardView>(R.id.rominfo)
                textView.visibility = INVISIBLE
                latestzipcard.visibility = INVISIBLE
                romincard.visibility = INVISIBLE
            }
        }

        fab.performClick()

    }


    // Checks internet access
    private fun checkNetwork(){
        val connected = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netinfo = connected.activeNetworkInfo
        networkAvail = netinfo != null && netinfo.isConnected
    }

    // Displays Update availability
    private fun updateReq() {
        doAsync {
            while (threadlink == ""){
                Thread.sleep(50)
            }

            val checkLatestArr = checkLatest(threadlink)
            val maintainerName = fetchMaintainer(threadlink)

            uiThread {
                if (checkLatestArr[0].toInt() < checkLatestArr[1].toInt()) {
                    MaterialDialog(this@ScrollingActivity).show {
                        icon(R.drawable.ic_update)
                        title(text = "Update available!")
                        message(text = "Latest Build: ${checkLatestArr[1]}\nDownload?")
                        positiveButton(text = "Yes") {
                            val openURL = Intent(Intent.ACTION_VIEW)
                            openURL.data = Uri.parse(latestLink)
                            startActivity(openURL)
                        }
                        negativeButton(text = "Cancel") { }
                    }
                }
                else {
                    MaterialDialog(this@ScrollingActivity).show {
                        icon(R.drawable.ic_checkmark)
                        title(text = "You are up-to-date!")
                        negativeButton(text = "Close") { }
                    }
                }

                val device = findViewById<TextView>(R.id.device)
                val yerdate = checkLatestArr[0]
                val buildDate = findViewById<TextView>(R.id.builddt)
                val maintainer = findViewById<TextView>(R.id.maintainer_name)

                device.text = android.os.Build.DEVICE

                buildDate.text = yerdate

                maintainer.text = maintainerName

                xda_thread.setOnClickListener {
                    MaterialDialog(this@ScrollingActivity).show {
                        title(text = "Are You sure?")
                        message(text = "This will open a browser window")
                        positiveButton(text = "Yes") {
                            val openURL = Intent(Intent.ACTION_VIEW)
                            openURL.data = Uri.parse(threadlink)
                            startActivity(openURL)
                        }
                        negativeButton(text = "Cancel") { }
                    }
                }

                fab.clearAnimation()
            }
        }
    }


    // Displays latest zip link
    private fun getLink() {
        doAsync {
            val latestButton = findViewById<Button>(R.id.lat_button)

            uiThread {
                latestButton.visibility = INVISIBLE
                toast("Checking for updates!")
            }

            val devicesArr = resources.getStringArray(R.array.devicearr)
            val dlPrefix = resources.getString(R.string.dlprefix)

            for (device in devicesArr){
                val thrddevarr = device.split("|")
                if (android.os.Build.DEVICE == thrddevarr[0]){
                    threadlink = thrddevarr[1]
                    latestLink = getDeviceLink(threadlink, dlPrefix)
                }
            }

            val romtitle = fetchTitle(threadlink)

            uiThread {
                val latName = findViewById<TextView>(R.id.lat_name)

                latName.text = romtitle

                latestButton.visibility = VISIBLE

                latestButton.setOnClickListener{
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse(latestLink)
                    startActivity(openURL)
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
