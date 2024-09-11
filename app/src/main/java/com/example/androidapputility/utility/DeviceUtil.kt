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
    }
}