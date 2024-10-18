package com.example.androidapputility.utility

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaCodecList
import android.opengl.GLES20
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.util.Log
import androidx.annotation.RequiresApi
import org.jetbrains.annotations.TestOnly
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader

class HardwareUtil {

    companion object {

        private val TAG = "HardwareUtil"

        fun getAvailableMemoryMB(context: Context): Long {
            val memoryInfo = getMemoryInfo(context)
            return memoryInfo.availMem / (1024 * 1024)
        }

        fun getAvailableMemoryMB(): Long {
            val runtime = Runtime.getRuntime()
            return runtime.maxMemory() / (1024 * 1024)
        }

        fun getFreeMemory(): Long {
            val runtime = Runtime.getRuntime()
            return runtime.freeMemory() / (1024 * 1024)
        }

        fun getTotalMemoryMB(context: Context): Long {
            val memoryInfo = getMemoryInfo(context)
            return memoryInfo.totalMem / (1024 * 1024)
        }

        fun getTotalMemoryMB(): Long {
            val runtime = Runtime.getRuntime()
            return runtime.totalMemory() / (1024 * 1024)
        }

        fun isLowMemory(context: Context): Boolean {
            val memoryInfo = getMemoryInfo(context)
            return memoryInfo.lowMemory
        }

        private fun getMemoryInfo(context: Context): ActivityManager.MemoryInfo {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            return memoryInfo
        }

        fun getCpuInfo(): String {
            val cpuInfoPath = "/proc/cpuinfo"
            val cpuInfo = StringBuilder()

            try {
                val reader = BufferedReader(FileReader(cpuInfoPath))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    cpuInfo.append(line).append("\n")
                }
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return cpuInfo.toString()
        }

        fun getGpuInfo(): HashMap<String, String?> {
            return HashMap<String, String?>().apply {
                put("GL_VENDOR", GLES20.glGetString(GLES20.GL_VENDOR))
                put("GL_RENDERER", GLES20.glGetString(GLES20.GL_RENDERER))
                put("GL_VERSION", GLES20.glGetString(GLES20.GL_VERSION))
                put(
                    "GL_SHADING_LANGUAGE_VERSION",
                    GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION)
                )
                put("GL_EXTENSIONS", GLES20.glGetString(GLES20.GL_EXTENSIONS))
            }
        }

        fun getSystemProperties(propertyName: String): String {
            var result = ""
            try {
                val process = Runtime.getRuntime().exec("getprop $propertyName")
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                result = reader.readLine()
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        fun getSupportedCodecs(): List<String> {
            val codecList = MediaCodecList(MediaCodecList.ALL_CODECS)
            val codecs = codecList.codecInfos
            return codecs.map { it.name }
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun getSupportedCodecs(isEncoder: Boolean): HashMap<String, Array<String>> {
            val codecList = MediaCodecList(MediaCodecList.ALL_CODECS) // Get all codec
            val codecInfos = codecList.codecInfos

            val codecs: HashMap<String, Array<String>> = HashMap<String, Array<String>>()
            for (codecInfo in codecInfos) {
                if (isEncoder == codecInfo.isEncoder) {
                    val codecName = codecInfo.name
                    codecs[codecInfo.name] = codecInfo.supportedTypes
                }
            }
            return codecs
        }

        fun getInternalStorageTotalSpaceMB(): Long {
            val internalStoragePath = Environment.getDataDirectory().path
            val internalStorageInfo = getStorageInfo(internalStoragePath)
            return internalStorageInfo.first
        }

        fun getInternalStorageAvailableSpaceMB(): Long {
            val internalStoragePath = Environment.getDataDirectory().path
            val internalStorageInfo = getStorageInfo(internalStoragePath)
            return internalStorageInfo.second
        }

        fun getExternalStorageTotalSpaceMB(): Long? {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val externalStoragePath = Environment.getExternalStorageDirectory().path
                val externalStorageInfo = getStorageInfo(externalStoragePath)
                return externalStorageInfo.first
            }
            return null
        }

        fun getExternalStorageAvailableSpaceMB(): Long? {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val externalStoragePath = Environment.getExternalStorageDirectory().path
                val externalStorageInfo = getStorageInfo(externalStoragePath)
                return externalStorageInfo.second
            }
            return null
        }

        private fun getStorageInfo(path: String): Pair<Long, Long> {
            val statFs = StatFs(path)
            val blockSize: Long
            val totalBlocks: Long
            val availableBlocks: Long

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = statFs.blockSizeLong
                totalBlocks = statFs.blockCountLong
                availableBlocks = statFs.availableBlocksLong
            } else {
                blockSize = statFs.blockSize.toLong()
                totalBlocks = statFs.blockCount.toLong()
                availableBlocks = statFs.availableBlocks.toLong()
            }

            // Total space (GB)
            val totalSpaceMB = (totalBlocks * blockSize) / (1024 * 1024)
            // Available Space (GB)
            val availableSpaceMB = (availableBlocks * blockSize) / (1024 * 1024)

            return Pair(totalSpaceMB, availableSpaceMB)
        }

        fun getAvailableSensors(context: Context): List<Sensor> {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            return sensorManager.getSensorList(Sensor.TYPE_ALL)
        }

        @RequiresApi(Build.VERSION_CODES.R)
        fun dumpStorageInfo(context: Context) {
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

            // Get all storage info
            val storageVolumes = storageManager.storageVolumes
            for (storageVolume in storageVolumes) {
                val uuid = storageVolume.uuid
                if (uuid != null) {
                    val storageVolumeDescription = storageVolume.getDescription(context)
                    Log.v(TAG, "storageVolumeDescription: $storageVolumeDescription")

                    val statsManager =
                        context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                    val storageUuid = storageManager.getUuidForPath(storageVolume.directory!!)
                    val totalSpace = statsManager.getTotalBytes(storageUuid) / (1024 * 1024 * 1024)
                    val freeSpace = statsManager.getFreeBytes(storageUuid) / (1024 * 1024 * 1024)

                    Log.v(TAG, "    totalSpace: $totalSpace GB")
                    Log.v(TAG, "    freeSpace: $totalSpace GB")
                }
            }
        }

        @TestOnly
        fun test(context: Context) {
            val a = getAvailableMemoryMB(context)
            val b = getTotalMemoryMB(context)

            val c = getAvailableMemoryMB()
            val d = getFreeMemory()
            val e = getTotalMemoryMB()
            val f = isLowMemory(context)

            val g = getInternalStorageTotalSpaceMB()
            val h = getInternalStorageAvailableSpaceMB()
            val i = getExternalStorageTotalSpaceMB()
            val j = getExternalStorageAvailableSpaceMB()

            val k = getAvailableSensors(context)

            val cpuInfo = getCpuInfo()
            val gpuInfo = getGpuInfo()
            val ro_hardware = getSystemProperties("ro.hardware")
            val ro_hardware_gpu = getSystemProperties("ro.hardware.gpu")
            val rp_board_platform = getSystemProperties("ro.board.platform")

            val supportedDecoder = getSupportedCodecs(false)
            val supportedEncoder = getSupportedCodecs(true)
        }
    }
}