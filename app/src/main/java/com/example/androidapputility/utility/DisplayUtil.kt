package com.example.androidapputility.utility

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.appcompat.app.AppCompatActivity

class DisplayUtil {

    enum class DENSITY_DPI_LEVEL {
        LDPI,
        MDPI,
        HDPI,
        XHDPI,
        XXHDPI,
        XXXHDPI,
        UNKNOWN
    }

    enum class SCREEN_ROTATION {
        _0,
        _90,
        _180,
        _270,
        UNKNOWN
    }

    enum class SCREEN_ORIENATION {
        UNSPECIFIED,        // 不指定方向，使用系統默認行為
        PORTRAIT,           // 直向模式
        LANDSCAPE,          // 橫向模式
        SENSOR,             // 根據感應器決定螢幕方向
        SENSOR_LANDSCAPE,   // 根據感應器自動旋轉，但僅限於橫向模式
        SENSOR_PORTRAIT,    // 根據感應器自動旋轉，但僅限於直向模式
        REVERSE_LANDSCAPE,  // 鎖定為反向橫向（即設備的橫向，但螢幕朝另一方向）
        REVERSE_PORTRAIT,   // 鎖定為反向直向
        UNDEFINED           // 尚未定義
    }

    enum class DISPLAY_TYPE {
        PRIMARY,
        SECONDARY,
    }

    companion object {

        fun getScreenSize(context: Context): Size {
            val displayMetrics = DisplayMetrics()

            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            return Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }

        fun getUsableScreenSize(context: Context): Size {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowManager =
                    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
                Size(windowMetrics.bounds.width(), windowMetrics.bounds.height())
            } else {
                val displayMetrics = DisplayMetrics()
                val windowManager =
                    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
            }
        }

        fun getDensity(context: Context): Float {
            val displayMetrics = DisplayMetrics()

            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            return displayMetrics.density
        }

        fun getDensityDpi(context: Context): Int {
            val displayMetrics = DisplayMetrics()

            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            return displayMetrics.densityDpi
        }

        fun getDensityDpiLevel(context: Context): DENSITY_DPI_LEVEL {
            val densityDpi = getDensityDpi(context)

            return when {
                densityDpi <= DisplayMetrics.DENSITY_LOW -> DENSITY_DPI_LEVEL.LDPI          // 120 dpi
                densityDpi <= DisplayMetrics.DENSITY_MEDIUM -> DENSITY_DPI_LEVEL.MDPI       // 160 dpi
                densityDpi <= DisplayMetrics.DENSITY_HIGH -> DENSITY_DPI_LEVEL.HDPI         // 240 dpi
                densityDpi <= DisplayMetrics.DENSITY_XHIGH -> DENSITY_DPI_LEVEL.XHDPI       // 320 dpi
                densityDpi <= DisplayMetrics.DENSITY_XXHIGH -> DENSITY_DPI_LEVEL.XXHDPI     // 480 dpi
                densityDpi <= DisplayMetrics.DENSITY_XXXHIGH -> DENSITY_DPI_LEVEL.XXXHDPI   // 640 dpi
                else -> DENSITY_DPI_LEVEL.UNKNOWN
            }
        }

        fun getScreenRotation(context: Context): SCREEN_ROTATION {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            return when (windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_0 -> SCREEN_ROTATION._0
                Surface.ROTATION_90 -> SCREEN_ROTATION._90
                Surface.ROTATION_180 -> SCREEN_ROTATION._180
                Surface.ROTATION_270 -> SCREEN_ROTATION._270
                else -> SCREEN_ROTATION.UNKNOWN
            }
        }

        fun getScreenOrientation(activity: AppCompatActivity): SCREEN_ORIENATION {
            return when (activity.requestedOrientation) {
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED -> SCREEN_ORIENATION.UNSPECIFIED
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> SCREEN_ORIENATION.PORTRAIT
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> SCREEN_ORIENATION.LANDSCAPE
                ActivityInfo.SCREEN_ORIENTATION_SENSOR -> SCREEN_ORIENATION.SENSOR
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> SCREEN_ORIENATION.SENSOR_LANDSCAPE
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT -> SCREEN_ORIENATION.SENSOR_PORTRAIT
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> SCREEN_ORIENATION.REVERSE_LANDSCAPE
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> SCREEN_ORIENATION.REVERSE_PORTRAIT
                else -> SCREEN_ORIENATION.UNDEFINED
            }
        }

        fun getAllDisplays(context: Context): HashMap<Int, DISPLAY_TYPE> {
            val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val displays = displayManager.getDisplays()
            return HashMap<Int, DISPLAY_TYPE>().apply {
                displays.map { display ->
                    put(display.displayId, getDisplayType(display))
                }
            }
        }

        private fun getDisplayType(display: Display): DISPLAY_TYPE {
            return when (display.displayId) {
                Display.DEFAULT_DISPLAY -> DISPLAY_TYPE.PRIMARY
                else -> DISPLAY_TYPE.SECONDARY
            }
        }

    }
}