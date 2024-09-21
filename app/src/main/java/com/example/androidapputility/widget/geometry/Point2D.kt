package com.example.androidapputility.widget.geometry

import android.graphics.Matrix
import android.graphics.PointF
import kotlin.math.sqrt

class Point2D {

    companion object {

        fun distance(p0: PointF, p1: PointF): Float {
            val dX = (p1.x - p0.x).toDouble()
            val dY = (p1.y - p0.y).toDouble()
            return sqrt(dX * dX + dY * dY).toFloat()
        }

        fun rotate(point: PointF, pivot: PointF, degree: Float): PointF {
            val p = floatArrayOf(point.x, point.y)
            Matrix().apply {
                setRotate(degree, pivot.x, pivot.y)
                mapPoints(p)
            }
            return PointF(p[0], p[1])
        }
    }
}