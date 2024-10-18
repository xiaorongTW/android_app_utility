package com.example.androidapputility.utility

class StringUtil {

    companion object {

        fun getFileExtension(filePath: String): String? {
            // "/storage/emulated/0/Download/example.txt" will return "txt"
            return filePath.substringAfterLast('.', "").takeIf { it.isNotEmpty() }
        }

        fun removeFileExtension(filePath: String): String {
            // "/storage/emulated/0/Download/example.txt" will return "example"
            return filePath.substringBeforeLast('.', filePath)
        }
    }
}