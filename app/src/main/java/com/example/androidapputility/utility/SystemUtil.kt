package com.example.androidapputility.utility

import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import java.util.Locale

class SystemUtil {

    companion object {

        fun getSystemLanguage(): String {
            return Locale.getDefault().language
        }

        fun getRingtoneVolume(context: Context): Int {
            return getVolume(context, AudioManager.STREAM_RING);
        }

        fun setRingtoneVolume(context: Context, volumeLevel: Int) {
            setVolume(context, AudioManager.STREAM_RING, volumeLevel)
        }

        fun adjustRingtoneVolume(context: Context, direction: Int = AudioManager.ADJUST_SAME) {
            adjustVolume(context, AudioManager.STREAM_RING, direction)
        }

        fun getMusicVolume(context: Context): Int {
            return getVolume(context, AudioManager.STREAM_MUSIC);
        }

        fun setMusicVolume(context: Context, volumeLevel: Int) {
            setVolume(context, AudioManager.STREAM_MUSIC, volumeLevel)
        }

        fun adjustMusicVolume(context: Context, direction: Int = AudioManager.ADJUST_SAME) {
            adjustVolume(context, AudioManager.STREAM_MUSIC, direction)
        }

        private fun getVolume(context: Context, streamType: Int): Int {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            return audioManager.getStreamVolume(streamType)
        }

        private fun setVolume(context: Context, streamType: Int, volumeLevel: Int) {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(streamType)
            val newVolume = volumeLevel.coerceIn(0, maxVolume)
            audioManager.setStreamVolume(streamType, newVolume, 0)
        }

        private fun adjustVolume(context: Context, streamType: Int, direction: Int) {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.adjustStreamVolume(streamType, direction, 0)
        }

        fun isAirplaneModeOn(context: Context): Boolean {
            return Settings.System.getInt(
                context.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON, 0
            ) != 0
        }

        fun isAutoRotateEnabled(context: Context): Boolean {
            return try {
                val autoRotate = Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.ACCELEROMETER_ROTATION
                )
                autoRotate == 1
            } catch (e: Settings.SettingNotFoundException) {
                false
            }
        }

        fun getScreenBrightness(context: Context): Int? {
            return try {
                // 0 ~ 255
                Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            } catch (e: Settings.SettingNotFoundException) {
                null
            }
        }

        fun isAutoBrightnessEnabled(context: Context): Boolean {
            return try {
                val autoBrightness = Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE
                )
                autoBrightness == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            } catch (e: Settings.SettingNotFoundException) {
                false
            }
        }

        fun getScreenTimeoutMS(context: Context): Int? {
            return try {
                Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
            } catch (e: Settings.SettingNotFoundException) {
                null
            }
        }

        fun getSystemFontScale(context: Context): Float {
            val configuration = context.resources.configuration
            val fontScale = configuration.fontScale
            return fontScale
        }
    }
}