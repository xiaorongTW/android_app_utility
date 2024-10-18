package com.example.androidapputility.utility

import android.content.Context
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ZipUtil {

    interface OnZipListener {
        fun onStart()
        fun onProgress(progress: Int)
        fun onComplete()
        fun onException(throwable: Throwable)
    }

    interface OnUnzipListener {
        fun onPrepare(zipEntry: ZipEntry)
        fun onStart()
        fun onProgress(progress: Int)
        fun onComplete()
        fun onException(throwable: Throwable)
    }

    companion object {

        fun zip(inputFilePath: String, zipFilePath: String, listener: (Int) -> Unit) {
            val sourceFile = File(inputFilePath)
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFilePath))).use { zos ->
                val updateStatusByFileSize = false
                if (updateStatusByFileSize)
                    compressFile(
                        sourceFile,
                        sourceFile.name,
                        zos,
                        FileUtil.getFileSize(sourceFile),
                        listener
                    )
                else
                    compressFile(
                        sourceFile,
                        sourceFile.name,
                        zos,
                        FileUtil.getFileCount(sourceFile),
                        listener
                    )
            }
        }

        fun zip(compressFileList: List<File>, outputZipFile: String, listener: (Int) -> Unit) {
            val outputStream = FileOutputStream(outputZipFile)
            compressFiles(compressFileList, outputStream, listener)
        }

        fun unzip(zipFile: String, outputFolder: String) {
            val buffer = ByteArray(1024)

            try {
                val zis = ZipInputStream(FileInputStream(zipFile))
                var zipEntry: ZipEntry? = zis.nextEntry

                while (zipEntry != null) {
                    val newFile = File(outputFolder, zipEntry.name)
                    if (zipEntry.isDirectory) {
                        newFile.mkdirs()
                    } else {
                        FileOutputStream(newFile).use { fos ->
                            var len: Int
                            while (zis.read(buffer).also { len = it } > 0) {
                                fos.write(buffer, 0, len)
                            }
                        }
                    }
                    zis.closeEntry()
                    zipEntry = zis.nextEntry
                }
                zis.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun zipRaw(context: Context, rawResId: Int, zipFilePath: String, entryName: String) {
            val buffer = ByteArray(1024)

            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFilePath))).use { zos ->

                context.resources.openRawResource(rawResId).use { inputStream ->
                    val zipEntry = ZipEntry(entryName)
                    zos.putNextEntry(zipEntry)

                    var len: Int
                    while (inputStream.read(buffer).also { len = it } > 0) {
                        zos.write(buffer, 0, len)
                    }
                    zos.closeEntry()
                }
            }
        }

        fun unzipRaw(
            context: Context,
            rawResId: Int,
            outputDir: String,
            listener: OnUnzipListener
        ) {
            val buffer = ByteArray(1024)

            var totalProgress = 0
            context.resources.openRawResource(rawResId).use { inputStream ->
                ZipInputStream(BufferedInputStream(inputStream)).use { zis ->
                    var entry = zis.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory) {
                            totalProgress++
                        }
                        listener?.onPrepare(entry)
                        entry = zis.nextEntry
                    }
                }
            }

            context.resources.openRawResource(rawResId).use { inputStream ->
                ZipInputStream(BufferedInputStream(inputStream)).use { zis ->
                    listener?.onStart()

                    var progress = 0

                    var zipEntry: ZipEntry? = zis.nextEntry
                    while (zipEntry != null) {
                        val newFile = File(outputDir, zipEntry.name)
                        if (zipEntry.isDirectory) {
                            newFile.mkdirs()
                        } else {
                            try {
                                FileOutputStream(newFile).use { fos ->
                                    var len: Int
                                    while (zis.read(buffer).also { len = it } > 0) {
                                        fos.write(buffer, 0, len)
                                    }
                                }
                            } catch (t: Throwable) {
                                listener?.onException(t)
                            }
                            progress++
                            listener?.onProgress(100 * progress / totalProgress)
                        }
                        zis.closeEntry()
                        zipEntry = zis.nextEntry
                    }

                    listener?.onComplete()
                }
            }
        }

        private fun compressFile(
            file: File,
            fileName: String,
            zos: ZipOutputStream,
            totalFiles: Int,
            progressCallback: (Int) -> Unit,
            currentFileCount: Int = 0
        ): Int {
            if (file.isHidden) {
                return currentFileCount
            }

            var updatedCount = currentFileCount

            if (file.isDirectory) {
                val files = file.listFiles()
                if (files.isNullOrEmpty()) {
                    zos.putNextEntry(ZipEntry("$fileName/"))
                    zos.closeEntry()
                    updatedCount++
                    progressCallback(updatedCount * 100 / totalFiles)
                } else {
                    for (childFile in files) {
                        updatedCount = compressFile(
                            childFile,
                            "$fileName/${childFile.name}",
                            zos,
                            totalFiles,
                            progressCallback,
                            updatedCount
                        )
                    }
                }
            } else {
                FileInputStream(file).use { fis ->
                    BufferedInputStream(fis).use { bis ->
                        val buffer = ByteArray(1024)
                        zos.putNextEntry(ZipEntry(fileName))
                        var len: Int
                        while (bis.read(buffer).also { len = it } > 0) {
                            zos.write(buffer, 0, len)
                        }
                        zos.closeEntry()
                        updatedCount++
                        progressCallback(updatedCount * 100 / totalFiles) // Update progress
                    }
                }
            }
            return updatedCount
        }

        private fun compressFile(
            file: File,
            fileName: String,
            zos: ZipOutputStream,
            totalSize: Long,
            progressCallback: (Int) -> Unit,
            currentSize: Long = 0
        ): Long {
            if (file.isHidden) {
                return currentSize
            }

            var updatedSize = currentSize

            if (file.isDirectory) {
                val files = file.listFiles()
                if (files.isNullOrEmpty()) {
                    zos.putNextEntry(ZipEntry("$fileName/"))
                    zos.closeEntry()
                    updatedSize += 0 // Directories have no size
                    progressCallback((updatedSize * 100 / totalSize).toInt()) // Update progress
                } else {
                    for (childFile in files) {
                        updatedSize = compressFile(
                            childFile,
                            "$fileName/${childFile.name}",
                            zos,
                            totalSize,
                            progressCallback,
                            updatedSize
                        )
                    }
                }
            } else {
                val fileSize = file.length()
                FileInputStream(file).use { fis ->
                    BufferedInputStream(fis).use { bis ->
                        val buffer = ByteArray(1024)
                        zos.putNextEntry(ZipEntry(fileName))
                        var len: Int
                        while (bis.read(buffer).also { len = it } > 0) {
                            zos.write(buffer, 0, len)
                            updatedSize += len // 更新當前壓縮的大小
                            progressCallback((updatedSize * 100 / totalSize).toInt()) // 更新進度
                        }
                        zos.closeEntry()
                    }
                }
            }
            return updatedSize
        }

        private fun compressFiles(
            files: List<File>,
            outputStream: OutputStream,
            listener: (Int) -> Unit
        ) {
            ZipOutputStream(outputStream).use { zos ->
                val totalSize = FileUtil.getAllFileSize(files)
                var currentSize = 0L
                for (file in files) {
                    currentSize =
                        compressFile(file, file.name, zos, totalSize, listener, currentSize)
                }
            }
        }
    }
}