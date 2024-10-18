package com.example.androidapputility.utility

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class IOUtil {

    companion object {

        fun readRawFile(context: Context, resourceId: Int): String {
            val inputStream = context.resources.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream))

            val content = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                content.append(line).append("\n")
                line = reader.readLine()
            }
            reader.close()

            return content.toString()
        }

    }
}