package com.example.androidapputility.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.androidapputility.R
import com.example.androidapputility.utility.BitmapUtil

class CloudyGlassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var updateMask = true
    private var maskBitmap: Bitmap? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.cloudy_glass, this, true)
    }

    fun setCloudyGlass(bitmap: Bitmap) {
        maskBitmap = bitmap
        updateMask = true
    }

    private fun updateMaskIfNeeded() {
        if (measuredWidth <= 0 || measuredHeight <= 0) return

        if (updateMask) {
            findViewById<ImageView>(R.id.iv_mask)?.let {
                var bitmap = BitmapUtil.fromRaw(context, R.raw.cloudy_glass)
                it.setImageBitmap(bitmap)
                updateMask = false
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        updateMaskIfNeeded()
    }
}