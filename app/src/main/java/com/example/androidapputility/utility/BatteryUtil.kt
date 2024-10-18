package com.example.androidapputility.utility

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryUtil {

    enum class BATTERY_HEALTH_STATUS {
        GOOD,
        OVERHEAT,
        DEAD,
        OVER_VOLTAGE,
        UNSPECIFIED_FAILURE,
        COLD,
        UNKNOWN
    }

    companion object {

        fun isBatteryCharging(context: Context): Boolean {
            val batteryStatus = getBatteryStatus(context)
            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
            return isCharging
        }

        fun getBatteryPercentage(context: Context): Float {
            val batteryStatus = getBatteryStatus(context)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct = level / scale.toFloat() * 100
            return batteryPct
        }

        fun getBatteryHealth(context: Context): BATTERY_HEALTH_STATUS {
            val batteryStatus = getBatteryStatus(context)
            val health = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1

            return when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> BATTERY_HEALTH_STATUS.GOOD
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> BATTERY_HEALTH_STATUS.OVERHEAT
                BatteryManager.BATTERY_HEALTH_DEAD -> BATTERY_HEALTH_STATUS.DEAD
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BATTERY_HEALTH_STATUS.OVER_VOLTAGE
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BATTERY_HEALTH_STATUS.UNSPECIFIED_FAILURE
                BatteryManager.BATTERY_HEALTH_COLD -> BATTERY_HEALTH_STATUS.COLD
                else -> BATTERY_HEALTH_STATUS.UNKNOWN
            }
        }

        fun getBatteryVoltage(context: Context): Int {
            val batteryStatus = getBatteryStatus(context)
            return batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
        }

        fun getBatteryTemperature(context: Context): Int {
            val batteryStatus = getBatteryStatus(context)
            return batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        }


        private fun getBatteryStatus(context: Context): Intent? {
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            return context.registerReceiver(null, intentFilter)
        }
    }
}