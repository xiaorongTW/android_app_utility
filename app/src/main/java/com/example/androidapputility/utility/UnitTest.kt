package com.example.androidapputility.utility

import android.content.Context
import com.example.androidapputility.R
import java.io.File

class UnitTest {

    companion object {
        private val CACHE_FOLDER_NAME = "androidAppUtility"
        private val CACHE_FOLDER_FILE =
            File(FileUtil.getExternalStorageDirectory(), CACHE_FOLDER_NAME)

        fun zipFileToExternalStorageDirectory(srcPath: String, listener: (Int) -> Unit) {
            ensureCacheFolderExist()
            val name = FileUtil.removeFileExtension(srcPath)
            ZipUtil.zip(
                srcPath,
                CACHE_FOLDER_FILE.absolutePath + File.separator + name + ".zip",
                listener
            )
        }

        fun zipFileListToExternalStorageDirectory(
            srcFileList: List<File>,
            listener: (Int) -> Unit
        ) {
            ensureCacheFolderExist()
            ZipUtil.zip(
                srcFileList,
                CACHE_FOLDER_FILE.absolutePath + File.separator + "files.zip",
                listener
            )
        }

        fun unzipRawFileToExternalStorageDirectory(
            context: Context,
            listener: ZipUtil.OnUnzipListener
        ) {
            ensureCacheFolderExist()
            ZipUtil.unzipRaw(context, R.raw.test_zip, CACHE_FOLDER_FILE.absolutePath, listener)
        }

        private fun ensureCacheFolderExist() {
            if (!CACHE_FOLDER_FILE.exists())
                CACHE_FOLDER_FILE.mkdirs()
        }
    }
}