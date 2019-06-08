package com.rupanshkek.generic_ota

import android.content.Context
import android.net.ConnectivityManager

object Networking {
    fun checkNetwork(context: Context): Boolean{
        val connected = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netinfo = connected.activeNetworkInfo
        return (netinfo != null && netinfo.isConnected)
    }
}