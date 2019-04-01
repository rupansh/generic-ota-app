package com.rupanshkek.relcafupdt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.coroutines.*
import org.jetbrains.anko.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket


class ScrollingActivity : AppCompatActivity() {

    private var networkAvail = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        fab.setOnClickListener {
            val networkTask = GlobalScope.async{ checkNetwork() }

            runBlocking{
                networkTask.await()
            }

            if (networkAvail) {
                updateReq()
                getLink()
            }
        }

        fab.performClick()
    }


    // Checks internet access
    private fun checkNetwork(){
        try {
            val timeoutMs = 1500
            val socket = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)

            socket.connect(socketAddress, timeoutMs)
            socket.close()

            networkAvail = true
        } catch (e: IOException) {
            doAsync{
                uiThread {
                    MaterialDialog(this@ScrollingActivity).show {
                        icon(R.drawable.ic_no_wifi)
                        title(text = "No Internet Access")
                        message(text = "Turn on Cellular Data/WiFi to fetch updates!")
                        negativeButton(text = "Quit") { finish(); moveTaskToBack(true) }
                        onDismiss { finish(); moveTaskToBack(true) }
                    }
                    val latestziptxt = findViewById<TextView>(R.id.latzip)
                    val textView = findViewById<TextView>(R.id.lat_link)
                    textView.visibility = INVISIBLE
                    latestziptxt.visibility = INVISIBLE
                }
            }
        }
    }

    // Displays Update availability
    private fun updateReq() {
        doAsync {
            val checkLatestArr = checkLatest()

            if (checkLatestArr[2].toBoolean()) {
                val latestLink = getDeviceLink()

                uiThread {
                    val yerdatetxt = findViewById<TextView>(R.id.yer_date_txt)
                    val yerdate = findViewById<TextView>(R.id.yer_date)
                    val ourdatetxt = findViewById<TextView>(R.id.our_date_txt)
                    val ourdate = findViewById<TextView>(R.id.our_date)

                    yerdatetxt.visibility = VISIBLE
                    ourdatetxt.visibility = VISIBLE
                    yerdate.visibility = VISIBLE
                    ourdate.visibility = VISIBLE
                    yerdate.text = checkLatestArr[0]
                    ourdate.text = checkLatestArr[1]

                    MaterialDialog(this@ScrollingActivity).show {
                        icon(R.drawable.ic_update)
                        title(text = "Update available!")
                        message(text = "Download?")
                        positiveButton(text = "Yes") {
                            val openURL = Intent(android.content.Intent.ACTION_VIEW)
                            openURL.data = Uri.parse(latestLink)
                            startActivity(openURL)
                        }
                        negativeButton(text = "Cancel") { }
                    }
                }
            }
        }
    }


    // Displays latest zip link
    private fun getLink() {
        doAsync {
            val textView = findViewById<TextView>(R.id.lat_link)
            val progressBar = findViewById<ProgressBar>(R.id.fetchbar)

            uiThread {
                textView.visibility = INVISIBLE
                progressBar.visibility = VISIBLE

                toast("Fetching link! please wait")
            }

            val linktext = getDeviceLink().split('/')
            val link = HtmlCompat.fromHtml(
                "<a href=${getDeviceLink()}>${linktext[linktext.lastIndex - 1]}</a>",
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )

            uiThread {
                textView.text = link
                textView.textSize = 15f
                progressBar.visibility = INVISIBLE
                textView.visibility = VISIBLE
                toast("Latest zip link fetched!")
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
