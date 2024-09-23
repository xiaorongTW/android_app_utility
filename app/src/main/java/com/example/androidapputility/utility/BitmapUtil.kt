package com.example.androidapputility.utility

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Size
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BitmapUtil {

    companion object {

        fun fromPath(path: String): Bitmap? {
            return try {
                BitmapFactory.decodeFile(path)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun fromRaw(context: Context, id: Int): Bitmap {
            val inputStream: InputStream = context.resources.openRawResource(id)
            return BitmapFactory.decodeStream(inputStream)
        }

        fun toDrawable(context: Context, bitmap: Bitmap): Drawable {
            return BitmapDrawable(context.resources, bitmap)
        }

        @Throws(IOException::class)
        fun saveAsPNG(context: Context, bitmap: Bitmap, folderPath: String, fileName: String) {
            val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver: ContentResolver = context.contentResolver
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, folderPath)
                val imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                resolver.openOutputStream(imageUri!!)
            } else {
                val folderFile = File(folderPath)
                if (!folderFile.exists()) {
                    folderFile.mkdir()
                }

                val image = File(folderFile, fileName + ".png")
                FileOutputStream(image)
            }

            fos?.let {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                it.flush()
                it.close()
            }
        }

        fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable)
                return drawable.bitmap

            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }

        fun flipBitmap(bitmap: Bitmap, hFlip: Boolean, vFlip: Boolean): Bitmap {
            val matrix = Matrix()
            matrix.postScale(
                (if (hFlip) -1 else 1).toFloat(),
                (if (vFlip) -1 else 1).toFloat(),
                bitmap.width / 2f,
                bitmap.height / 2f
            )
            return Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }

        fun rotateBitmap(srcBitmap: Bitmap, degree: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degree)
            return Bitmap.createBitmap(
                srcBitmap,
                0,
                0,
                srcBitmap.width,
                srcBitmap.height,
                matrix,
                true
            )
        }

        fun cropBitmap(srcBitmap: Bitmap, roi: RectF): Bitmap {
            val cropBitmapSize = Size(
                (roi.width() * srcBitmap.width).toInt(),
                (roi.height() * srcBitmap.height).toInt()
            )
            val cropBitmapRect = Rect(
                (roi.left * srcBitmap.width).toInt(),
                (roi.top * srcBitmap.height).toInt(),
                (roi.right * srcBitmap.width).toInt(),
                (roi.bottom * srcBitmap.height).toInt()
            )

            val croppedBitmap = Bitmap.createBitmap(
                cropBitmapSize.width,
                cropBitmapSize.height,
                Bitmap.Config.ARGB_8888
            )
            Canvas(croppedBitmap).apply {
                drawBitmap(
                    srcBitmap,
                    Rect(
                        cropBitmapRect.left,
                        cropBitmapRect.top,
                        cropBitmapRect.right,
                        cropBitmapRect.bottom
                    ),
                    Rect(0, 0, cropBitmapSize.width, cropBitmapSize.height),
                    null
                )
            }
            return croppedBitmap
        }

        fun blurBitmap(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
            val renderScript = RenderScript.create(context)
            val input = Allocation.createFromBitmap(renderScript, bitmap)
            val output = Allocation.createTyped(renderScript, input.type)
            val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

            script.setRadius(radius)
            script.setInput(input)
            script.forEach(output)
            output.copyTo(bitmap)

            renderScript.destroy()
            return bitmap
        }
    }
}