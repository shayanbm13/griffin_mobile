package com.example.griffinmobile.modules

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
fun connectToWiFiAndPerformAction(
    context: Context,
    ssid: String,
    password: String?,
    onConnected: () -> Unit,
    onTimeout: () -> Unit
) {
    val timeoutHandler = Handler(Looper.getMainLooper())
    var networkCallback: ConnectivityManager.NetworkCallback? = null
    var networkId: Int? = null

    val timeoutRunnable = Runnable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            networkCallback?.let {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                connectivityManager.unregisterNetworkCallback(it) // لغو تلاش برای اتصال
            }
        } else {
            networkId?.let { id ->
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiManager.disableNetwork(id) // غیرفعال کردن شبکه
                wifiManager.disconnect()
            }
        }
        onTimeout() // عملیات هنگام تایم‌اوت
    }

    val timeoutMillis = 30000L // زمان تایم‌اوت (15 ثانیه)
    timeoutHandler.postDelayed(timeoutRunnable, timeoutMillis)

    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            networkCallback = connectToWiFiAndroid10AndAbove(context, ssid, password) {
                timeoutHandler.removeCallbacks(timeoutRunnable) // حذف تایم‌اوت در صورت اتصال موفق
                onConnected()
            }
        }
        else -> {
            networkId = connectToWiFiBelowAndroid10(context, ssid, password) {
                timeoutHandler.removeCallbacks(timeoutRunnable) // حذف تایم‌اوت در صورت اتصال موفق
                onConnected()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun connectToWiFiAndroid10AndAbove(
    context: Context,
    ssid: String,
    password: String?,
    onConnected: () -> Unit
): ConnectivityManager.NetworkCallback {
    val wifiNetworkSpecifierBuilder = android.net.wifi.WifiNetworkSpecifier.Builder()
        .setSsid(ssid)

    if (!password.isNullOrEmpty()) {
        wifiNetworkSpecifierBuilder.setWpa2Passphrase(password)
    }

    val wifiNetworkSpecifier = wifiNetworkSpecifierBuilder.build()

    val networkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .setNetworkSpecifier(wifiNetworkSpecifier)
        .build()

    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            connectivityManager.bindProcessToNetwork(network) // اتصال به شبکه
            onConnected() // عملیات پس از اتصال
        }

        override fun onUnavailable() {
            super.onUnavailable()
            // مدیریت خطا در صورت عدم موفقیت
        }
    }

    connectivityManager.requestNetwork(networkRequest, networkCallback)
    return networkCallback
}

private fun connectToWiFiBelowAndroid10(
    context: Context,
    ssid: String,
    password: String?,
    onConnected: () -> Unit
): Int? {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    val wifiConfig = WifiConfiguration().apply {
        SSID = "\"$ssid\""
        if (!password.isNullOrEmpty()) {
            preSharedKey = "\"$password\""
        } else {
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        }
    }

    val networkId = wifiManager.addNetwork(wifiConfig)
    if (networkId != -1) {
        wifiManager.disconnect()
        wifiManager.enableNetwork(networkId, true)
        wifiManager.reconnect()

        val isConnected = checkWiFiConnection(context, ssid)
        if (isConnected) {
            onConnected()
        }
    }
    return networkId
}

// بررسی وضعیت اتصال به وای‌فای
private fun checkWiFiConnection(context: Context, ssid: String): Boolean {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val connectionInfo = wifiManager.connectionInfo
    return connectionInfo != null && connectionInfo.ssid == "\"$ssid\""
}