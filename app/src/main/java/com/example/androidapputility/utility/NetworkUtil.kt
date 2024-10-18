package com.example.androidapputility.utility

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.NETWORK_TYPE_NR
import androidx.core.app.ActivityCompat

class NetworkUtil {

    companion object {

        fun isNetworkConnected(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        fun getNetworkConnectedType(context: Context): String {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                networkCapabilities?.let {
                    when {
                        it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return "Wi-Fi"
                        it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return "Mobile-Network"
                        it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return "Ethernet"
                        else -> return "Unknown"
                    }
                }
            } else {
                //  Handling for old Android API version
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                activeNetworkInfo?.let {
                    when (it.type) {
                        ConnectivityManager.TYPE_WIFI -> return "Wi-Fi"
                        ConnectivityManager.TYPE_MOBILE -> return "Mobile-Network"
                        ConnectivityManager.TYPE_ETHERNET -> return "Ethernet"
                        else -> return "Unknown"
                    }
                }
            }
            return "Network-Disconnected"
        }

        fun isHighPerformanceNetwork(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                networkCapabilities?.let {
                    return it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                            (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                    isConnectedTo5G(context, null))
                }
            }
            return false
        }

        fun isConnectedTo5G(context: Context, requestPermissionRunnable: Runnable?): Boolean {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionRunnable?.run()
                return false
            }

            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val networkType = telephonyManager.dataNetworkType
            return networkType == NETWORK_TYPE_NR // 5G New Radio
        }

        fun getMobileNetworkType(context: Context, requestPermissionRunnable: Runnable?): Int? {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionRunnable?.run()
                return null
            }

            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            // 2G:      NETWORK_TYPE_GPRS, NETWORK_TYPE_EDGE
            // 3G:      NETWORK_TYPE_HSPA, NETWORK_TYPE_HSPAP
            // 4G LTE:  NETWORK_TYPE_LTE
            // 5G:      NETWORK_TYPE_NR
            return telephonyManager.dataNetworkType
        }

        fun getNetworkSpeed(context: Context): String {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                networkCapabilities?.let {
                    if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return "Wi-Fi speed: ${it.linkDownstreamBandwidthKbps} kbps"
                    } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return "Mobile-Network speed: ${it.linkDownstreamBandwidthKbps} kbps"
                    }
                }
            }
            return "No speed available"
        }

        fun getSignalStrength(context: Context) {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val phoneStateListener = object : PhoneStateListener() {
                override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                    super.onSignalStrengthsChanged(signalStrength)
                    val signalLevel = signalStrength.level
                    println("Signal strength: $signalLevel / 4")
                }
            }
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
        }

        fun getWifiSignalStrength(context: Context) {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo

            val rssi = wifiInfo.rssi  // WiFi 訊號強度（RSSI）
            val level = WifiManager.calculateSignalLevel(rssi, 5)  // 將 RSSI 轉換為訊號強度等級（0 到 4）

            println("WiFi signal strength: $level / 4")
        }
    }
}