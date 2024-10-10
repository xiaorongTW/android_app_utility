package com.example.androidapputility.utility

import android.content.Context
import android.os.Environment
import java.io.File

class FileUtil {

    companion object {

        fun getExternalStorageDirectory(): File {
            return Environment.getExternalStorageDirectory()
        }

        fun getInternalStorageDirectory(context: Context): File {
            return context.filesDir
        }

        fun isFilePath(path: String): Boolean {
            val file = File(path)
            if (file.exists() && file.isFile)
                return true

            val regex = Regex("""^(file://|/|\\\\).+""")
            return regex.matches(path) || path.contains(".")
        }

        fun getFileExtension(path: String): String? {
            // Path "/storage/emulated/0/Download/example.txt" will return "txt"
            val file = File(path)
            return file.extension.takeIf { it.isNotEmpty() }
        }

        fun removeFileExtension(path: String): String {
            // Path "/storage/emulated/0/Download/example.txt" will return "example"
            val file = File(path)
            return if (file.extension.isNotEmpty()) {
                file.nameWithoutExtension
            } else {
                file.name
            }
        }

        fun hadExtension(path: String, ext: String): Boolean {
            return getFileExtension(path)?.equals(ext, ignoreCase = true) ?: false
        }

        fun getFileCount(file: File): Int {
            var count = 0
            if (file.isDirectory) {
                val files = file.listFiles()
                files?.forEach { childFile ->
                    count += getFileCount(childFile)
                }
            } else {
                count++
            }
            return count
        }

        fun getFileSize(file: File): Long {
            var totalSize = 0L
            if (file.isDirectory) {
                val files = file.listFiles()
                files?.forEach { childFile ->
                    totalSize += getFileSize(childFile)
                }
            } else {
                totalSize += file.length()
            }
            return totalSize
        }

        fun getAllFileSize(files: List<File>): Long {
            var totalSize = 0L
            for (file in files) {
                if (file.isDirectory) {
                    val childFiles = file.listFiles()
                    totalSize += if (childFiles != null) {
                        getAllFileSize(childFiles.toList())
                    } else {
                        0L
                    }
                } else {
                    totalSize += file.length()
                }
            }
            return totalSize
        }

        fun createFolder(path: String) {
            val file = File(path)
            if (!file.exists())
                file.mkdirs()
        }

        fun createFolder(parentPath: String, folderName: String): File {
            val file = File(parentPath, folderName)
            if (!file.exists())
                file.mkdirs()
            return file
        }

        fun clearFolder(path: File) {
            if (path.exists() && path.isDirectory) {
                path.listFiles()?.forEach { file ->
                    if (file.isFile)
                        file.delete()
                    else if (file.isDirectory) {
                        clearFolder(file)
                        file.delete()
                    }
                }
            }
        }

        fun deleteFolder(path: File): Boolean {
            if (path.exists() && path.isDirectory) {
                return path.deleteRecursively()
            }
            return false
        }

    }
}