package com.example.androidapputility.utility

import android.graphics.PointF
import kotlin.math.sqrt

class Line {
    private var p0: PointF = PointF(0f, 0f)
    private var p1: PointF = PointF(0f, 0f)

    constructor(start: PointF, end: PointF) {
        p0 = start
        p1 = end
    }

    private fun getPointX(y: Float): Float? {
        return if (p1.y == p0.y)
            null
        else {
            val m10 = (p1.x - p0.x) / (p1.y - p0.y)
            p0.x + m10 * (y - p0.y)
        }
    }

    private fun getPointY(x: Float): Float? {
        return if (p1.x == p0.x)
            null
        else {
            val m10 = (p1.y - p0.y) / (p1.x - p0.x)
            p0.y + m10 * (x - p0.x)
        }
    }

    fun distance(): Float {
        val dX = (p1.x - p0.x).toDouble()
        val dY = (p1.y - p0.y).toDouble()
        return sqrt(dX * dX + dY * dY).toFloat()
    }

    fun isPassed(p: PointF): Boolean {
        // Check if the point satisfies the line equation
        val dx1 = p.x - p0.x
        val dy1 = p.y - p0.y
        val dx2 = p1.x - p0.x
        val dy2 = p1.y - p0.y

        val crossProduct = dx1 * dy2 - dy1 * dx2
        if (crossProduct != 0.0f) return false

        // Check if the point is within the bounds of the line segment
        val dotProduct = dx1 * dx2 + dy1 * dy2
        if (dotProduct < 0.0) return false

        val squaredLength = dx2 * dx2 + dy2 * dy2
        if (dotProduct > squaredLength) return false

        return true
    }


}