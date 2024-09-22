package com.example.androidapputility.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.androidapputility.utility.UIUtil
import kotlin.math.abs

internal class DegreeRulerView : View {
    companion object {
        private const val TAG = "[DegreeRulerView]"
        private const val DEBUG = false
        const val DEGREE_SYMBOL: Char = 0x00B0.toChar()
        const val DEGREE_TEXT_SIZE: Float = 12.0f
    }

    private var degreePointerViewH: Float = 0f

    private var pxPerDegree = 0
    private var degreeRange = 0
    private var margin = 0
    private var scrollX = 0
    private var scrolledDegree = 0f
    private var showDegreeText = true

    private val normalColor = -0x7f000001
    private val normalPaint = Paint()

    private val highlightColor = -0xda2802
    private val highlightPaint = Paint()

    private val normalPaintText = Paint()

    private val highlightPaintText = Paint()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        degreePointerViewH = UIUtil.convertPxToDp(context, 300f)

        normalPaint.color = normalColor

        normalPaintText.color = normalColor
        normalPaintText.textSize = DEGREE_TEXT_SIZE * resources.displayMetrics.density
        normalPaintText.isAntiAlias = true
        normalPaintText.textAlign = Paint.Align.CENTER

        highlightPaint.color = highlightColor

        highlightPaintText.color = highlightColor
        highlightPaintText.textSize = DEGREE_TEXT_SIZE * resources.displayMetrics.density
        highlightPaintText.isAntiAlias = true
        highlightPaintText.textAlign = Paint.Align.CENTER
    }

    fun setupParameters(degreeRange: Int, pxPerDegree: Int, margin: Int) {
        this.degreeRange = degreeRange
        this.pxPerDegree = pxPerDegree
        this.margin = margin

        layoutParams.width = (degreeRange - -degreeRange) * pxPerDegree + 2 * margin
        requestLayout()
    }

    fun getScrolledDegree(scrollX: Int): Float {
        this.scrollX = scrollX
        invalidate()

        val progress = 1.0f * scrollX / (width - 2 * margin)
        val scrolledDegree = (-degreeRange + 2 * degreeRange * progress)
        if (this.scrolledDegree != scrolledDegree) this.scrolledDegree = scrolledDegree
        return scrolledDegree
    }

    fun getScrollXByDegree(degree: Float): Int {
        return ((degree + degreeRange) * (width - 2 * margin) / (2 * degreeRange)).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        if (layoutParams.width <= 0) return

        val centerX = layoutParams.width / 2
        val startY = 2 * height / 5

        val strokeWidthScalar = 5
        val degree15OffsetY = (0.8f * degreePointerViewH).toInt()
        val degreeOffsetY = (0.5 * degreePointerViewH).toInt()
        val normalTextOffsetY = (2 * degreePointerViewH).toInt()

        normalPaint.strokeWidth = strokeWidthScalar.toFloat()
        highlightPaint.strokeWidth = strokeWidthScalar.toFloat()

        for (i in -degreeRange..degreeRange) {
            val degreeX = centerX + i * pxPerDegree
            val text = "" + i + DEGREE_SYMBOL

            if (abs((margin + scrollX - degreeX).toDouble()) < pxPerDegree / 2) {
                if (i % 15 == 0) {
                    if (showDegreeText) canvas.drawText(
                        "" + text,
                        degreeX.toFloat(),
                        (startY + normalTextOffsetY).toFloat(),
                        normalPaintText
                    )
                }
            } else {
                val startX = degreeX - strokeWidthScalar / 2
                if (i % 15 == 0) {
                    canvas.drawLine(
                        startX.toFloat(),
                        startY.toFloat(),
                        startX.toFloat(),
                        (startY + degree15OffsetY).toFloat(),
                        normalPaint
                    )
                    if (showDegreeText) canvas.drawText(
                        "" + text,
                        degreeX.toFloat(),
                        (startY + normalTextOffsetY).toFloat(),
                        normalPaintText
                    )
                } else {
                    canvas.drawLine(
                        startX.toFloat(),
                        startY.toFloat(),
                        startX.toFloat(),
                        (startY + degreeOffsetY).toFloat(),
                        normalPaint
                    )
                }
            }
        }
    }
}