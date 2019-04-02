package com.rupanshkek.relcafupdt

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import android.widget.*
import androidx.core.text.HtmlCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.parse.Parse
import kotlinx.android.synthetic.main.activity_scrolling.*
import org.jetbrains.anko.*


class ScrollingActivity : AppCompatActivity() {

    private var networkAvail = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        fab.setOnClickListener {
            checkNetwork()

            if (networkAvail) {
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
                val latestziptxt = findViewById<TextView>(R.id.latzip)
                val textView = findViewById<TextView>(R.id.lat_link)
                textView.visibility = INVISIBLE
                latestziptxt.visibility = INVISIBLE
            }
        }

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.appid))
                .clientKey(getString(R.string.clientkey))
                .server("https://parseapi.back4app.com")
                .build()
        )

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
            val checkLatestArr = CheckingUpdates.checkLatest()
            CheckingUpdates.checkTesting()

            while(!CheckingUpdates.isTestingSet){
                Thread.sleep(500)
            }

            val checkTestingArr = CheckingUpdates.testingInfo

            if (checkLatestArr[0].toInt() < checkLatestArr[1].toInt()) {
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

            if(checkTestingArr[0] != "not avail"){
                val testinglink = checkTestingArr[0]
                val testingdate = checkTestingArr[1]!!.replace("-", "")
                val yerdate = checkLatestArr[0]
                val ourdate = checkLatestArr[1]

                if(yerdate.toInt() < testingdate.toInt() && (testingdate.toInt() > ourdate.toInt())) {
                    uiThread {
                        val testingtxt = findViewById<TextView>(R.id.testing_date_txt)

                        testingtxt.visibility = VISIBLE
                        testing_button.visibility = VISIBLE

                        testing_button.setOnClickListener {
                            MaterialDialog(this@ScrollingActivity).show {
                                icon(R.drawable.ic_warning)
                                title(text = "Warning!")
                                message(text = "Testing builds are prone to bugs! Make sure to perform a backup before flashing them. We are not responsible for bricked devices! ")
                                positiveButton(text = "Download Anyways") {
                                    val openURL = Intent(android.content.Intent.ACTION_VIEW)
                                    openURL.data = Uri.parse(testinglink)
                                    startActivity(openURL)
                                }
                                negativeButton(text = "Cancel") { }
                            }
                        }
                        toast("Testing Build Available!")
                    }
                }
            }

            if(checkTestingArr[0] == "not avail" && checkLatestArr[0].toInt() > checkLatestArr[1].toInt()){
                uiThread {
                    val testingtxt = findViewById<TextView>(R.id.testing_date_txt)

                    testingtxt.visibility = INVISIBLE
                    testing_button.visibility = INVISIBLE

                    MaterialDialog(this@ScrollingActivity).show {
                        icon(R.drawable.ic_checkmark)
                        title(text = "You are up-to-date!")
                        negativeButton(text = "Close") { }
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
                toast("Checking for updates!")
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
