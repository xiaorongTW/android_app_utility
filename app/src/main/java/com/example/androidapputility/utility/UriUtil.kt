package com.example.androidapputility.utility

import android.app.Activity
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.File

class UriUtil {

    companion object {

        private fun getFileFromUri(activity: Activity, uri: Uri): File? {
            var filePath: String? = null

            val contentResolver = activity.contentResolver
            if (DocumentsContract.isDocumentUri(activity, uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]
                when (type) {
                    "image" -> {
                        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        filePath =
                            getDataColumn(contentResolver, contentUri, "_id=?", arrayOf(split[1]))
                    }

                    "video" -> {
                        val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        filePath =
                            getDataColumn(contentResolver, contentUri, "_id=?", arrayOf(split[1]))
                    }

                    "audio" -> {
                        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        filePath =
                            getDataColumn(contentResolver, contentUri, "_id=?", arrayOf(split[1]))
                    }

                    "document" -> {
                        val contentUri = MediaStore.Files.getContentUri("external")
                        filePath =
                            getDataColumn(contentResolver, contentUri, "_id=?", arrayOf(split[1]))
                    }

                    "downloads" -> {
                        val downloadsUri = Uri.parse("content://downloads/public_downloads")
                        val contentUri = Uri.withAppendedPath(downloadsUri, split[1])
                        filePath = getDataColumn(contentResolver, contentUri, null, null)
                    }

                    else -> {
                        return getFileFromStream(contentResolver, uri)
                    }
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                filePath = getDataColumn(contentResolver, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                filePath = uri.path
            }

            return filePath?.let { File(it) }
        }

        private fun getDataColumn(
            contentResolver: ContentResolver,
            uri: Uri,
            selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    return cursor.getString(columnIndex)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        private fun getFileFromStream(contentResolver: ContentResolver, uri: Uri): File? {
            val fileName = getFileName(contentResolver, uri)
            val tempFile = File.createTempFile("temp", fileName)
            tempFile.outputStream().use { outputStream ->
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return tempFile
        }

        private fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
            var name = "temp_file"
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        name = it.getString(nameIndex)
                    }
                }
            }
            return name
        }

        fun toFile(activity: Activity, uri: Uri): File? {
            if (uri.scheme == "content") {
                return getFileFromUri(activity, uri)
            } else if (uri.scheme == "file") {
                return File(uri.path ?: "")
            }
            return null
        }
    }
}