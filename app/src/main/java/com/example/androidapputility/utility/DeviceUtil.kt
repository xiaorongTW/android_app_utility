package com.example.androidapputility.utility

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.telephony.TelephonyManager

class DeviceUtil {

    companion object {

        fun isChromebookDevice(context: Context): Boolean {
            if (context.packageManager.hasSystemFeature("org.chromium.arc")) return true

            // Reference: https://stackoverflow.com/questions/39784415/how-to-detect-programmatically-if-android-app-is-running-in-chrome-book-or-in
            val ARC_DEVICE_PATTERN = ".+_cheets|cheets_.+"
            return (context.packageManager.hasSystemFeature("org.chromium.arc.device_management")
                    || (Build.DEVICE != null && Build.DEVICE.matches(ARC_DEVICE_PATTERN.toRegex())))
        }

        fun isTabletDevice(context: Context): Boolean {
            val configuration = context.resources.configuration

            // Check the screen size is large or xlarge, which indicates it's a tablet
            return (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
        }

        fun isMobileDevice(context: Context): Boolean {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val isPhone = telephonyManager.phoneType != TelephonyManager.PHONE_TYPE_NONE

            val configuration = context.resources.configuration
            val isScreenSizeMobile =
                (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) <= Configuration.SCREENLAYOUT_SIZE_NORMAL

            return isPhone && isScreenSizeMobile
        }

        fun getDeviceBuildInfo(): HashMap<String, String> {
            val buildInfo = HashMap<String, String>().apply {
                put("BRAND", Build.BRAND)
                put("BOARD", Build.BOARD)
                put("DEVICE", Build.DEVICE)
                put("HARDWARE", Build.HARDWARE)
                put("MODEL", Build.MODEL)
                put("MANUFACTURER", Build.MANUFACTURER)
                put("OS_VER", Build.VERSION.RELEASE)
                put("PRODUCT", Build.PRODUCT)
                put("VERSION.RELEASE", Build.VERSION.RELEASE)           // Android version
                put("VERSION.SDK_INT", "" + Build.VERSION.SDK_INT)      // SDK version
                put("VERSION.CODENAME", "" + Build.VERSION.CODENAME)    // Version name
                put(
                    "VERSION.CODENAME",
                    Build.SUPPORTED_ABIS.joinToString(", ")
                ) // Support cpu architecture
                put(
                    "VERSION.CODENAME",
                    Build.SUPPORTED_32_BIT_ABIS.joinToString(", ")
                ) // Support 32-bit architecture
                put(
                    "VERSION.CODENAME",
                    Build.SUPPORTED_64_BIT_ABIS.joinToString(", ")
                ) // Support 64-bit architecture
            }
            return buildInfo
        }

    }
}