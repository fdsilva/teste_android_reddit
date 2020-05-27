package com.fastnews.mechanism

import android.content.Context
import android.net.ConnectivityManager

class VerifyNetworkInfo(private val context: Context) {

    fun isConnected(): Boolean {
        var isConnected = false
        val activeNetwork = getActiveNetworkInfo()
        if (activeNetwork != null) {
            isConnected = activeNetwork.isConnected
        }
        return isConnected
    }

    private fun getActiveNetworkInfo(): android.net.NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

}
