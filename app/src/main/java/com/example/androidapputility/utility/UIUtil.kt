package com.example.androidapputility.utility

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import kotlin.math.max
import kotlin.math.min

class UIUtil {

    companion object {

        fun isPortrait(context: Context): Boolean {
            return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        }

        fun convertDpToPx(context: Context, dp: Float): Float {
            val metrics: DisplayMetrics = context.resources.displayMetrics
            val px = dp * (metrics.densityDpi / 160f)
            return px
        }

        fun convertPxToDp(context: Context, px: Float): Float {
            val metrics: DisplayMetrics = context.resources.displayMetrics
            val dp = px / (metrics.densityDpi / 160f)
            return dp
        }

        fun getScreenWidthInLandscape(context: Context): Int {
            try {
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val metrics = DisplayMetrics()
                display.getMetrics(metrics)

                if (context.resources.configuration.orientation === Configuration.ORIENTATION_PORTRAIT)
                    return max(
                        metrics.widthPixels.toDouble(),
                        metrics.heightPixels.toDouble()
                    ).toInt()

                return metrics.widthPixels
            } catch (e: Throwable) {
                return 1920 // allow ADT to go
            }
        }

        fun getScreenHeightInLandscape(context: Context): Int {
            try {
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val metrics = DisplayMetrics()
                display.getMetrics(metrics)

                if (context.resources.configuration.orientation === Configuration.ORIENTATION_PORTRAIT)
                    return min(
                        metrics.widthPixels.toDouble(),
                        metrics.heightPixels.toDouble()
                    ).toInt()

                return metrics.heightPixels
            } catch (e: Throwable) {
                return 1200 // allow ADT to go
            }
        }

        fun getScreenWidth(context: Context, isPortrait: Boolean): Int {
            try {
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val metrics = DisplayMetrics()
                display.getMetrics(metrics)

                return if (isPortrait)
                    min(
                        metrics.widthPixels.toDouble(),
                        metrics.heightPixels.toDouble()
                    ).toInt()
                else max(
                    metrics.widthPixels.toDouble(),
                    metrics.heightPixels.toDouble()
                ).toInt()
            } catch (e: Throwable) {
                return 1920 // allow ADT to go
            }
        }

        fun getScreenHeight(context: Context, isPortrait: Boolean): Int {
            try {
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val metrics = DisplayMetrics()
                display.getMetrics(metrics)

                return if (isPortrait)
                    max(
                        metrics.widthPixels.toDouble(),
                        metrics.heightPixels.toDouble()
                    ).toInt()
                else
                    min(
                        metrics.widthPixels.toDouble(),
                        metrics.heightPixels.toDouble()
                    ).toInt()
            } catch (e: Throwable) {
                return 1200 // allow ADT to go
            }
        }

        fun getCurrentScreenWidth(context: Context): Int {
            try {
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val metrics = DisplayMetrics()
                display.getMetrics(metrics)

                return metrics.widthPixels
            } catch (e: Throwable) {
                return 1920 // allow ADT to go
            }
        }

        fun getCurrentScreenHeight(context: Context): Int {
            try {
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val metrics = DisplayMetrics()
                display.getMetrics(metrics)

                return metrics.heightPixels
            } catch (e: Throwable) {
                return 1200 // allow ADT to go
            }
        }

        fun getRawScreenHeight(context: Context): Int {
            try {
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val size = Point()
                display.getRealSize(size)
                if (context.resources.configuration.orientation === Configuration.ORIENTATION_PORTRAIT)
                    return min(size.x.toDouble(), size.y.toDouble()).toInt()
                return size.y
            } catch (e: Throwable) {
                return 1200 // allow ADT to go
            }
        }

        fun getRawScreenHeight(context: Context, isPortrait: Boolean): Int {
            try {
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val size = Point()
                display.getRealSize(size)

                return if (isPortrait)
                    max(size.x.toDouble(), size.y.toDouble()).toInt()
                else
                    min(size.x.toDouble(), size.y.toDouble()).toInt()
            } catch (e: Throwable) {
                return 1200 // allow ADT to go
            }
        }

        fun getScreenMaxEdge(context: Context): Int {
            try {
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val metrics = DisplayMetrics()
                display.getMetrics(metrics)
                return max(metrics.widthPixels.toDouble(), metrics.heightPixels.toDouble()).toInt()
            } catch (e: Throwable) {
                return 1920 // Allow ADT to go
            }
        }

        fun getScreenMinEdge(context: Context): Int {
            try {
                val display =
                    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val metrics = DisplayMetrics()
                display.getMetrics(metrics)
                return min(metrics.widthPixels.toDouble(), metrics.heightPixels.toDouble()).toInt()
            } catch (e: Throwable) {
                return 0
            }
        }

        fun copyStringToClipBoard(context: Context, label: String?, text: String?) {
            // Get the clipboard manager from the system
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // Create a ClipData with a label and the text you want to copy
            val clip = ClipData.newPlainText("Copied Text", text)

            // Set the clipboard content
            clipboard.setPrimaryClip(clip)
        }

        fun dumpCallStack(tag: String) {
            for (ste in Thread.currentThread().stackTrace) {
                Log.w(tag, ste.toString())
            }
        }
    }
}