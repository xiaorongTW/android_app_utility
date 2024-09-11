package com.example.androidapputility.utility

import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.SizeF
import androidx.core.graphics.toRectF

class RectUtil {

    companion object {

        fun toPoints(rect: RectF): Array<PointF> {
            val points = Array<PointF>(4) { PointF() }
            points[0].x = rect.left
            points[0].y = rect.top
            points[1].x = rect.right
            points[1].y = rect.top
            points[2].x = rect.right
            points[2].y = rect.bottom
            points[3].x = rect.left
            points[3].y = rect.bottom
            return points
        }

        fun toPoints(rect: Rect): Array<PointF> {
            return toPoints(rect.toRectF())
        }

        fun transformToRect(rect: Rect, offsetXY: SizeF?, scaleXY: SizeF?, degree: Float?): RectF {
            val resultRect = RectF(rect)

            scaleXY?.let {
                Matrix().apply {
                    setScale(it.width, it.height, resultRect.centerX(), resultRect.centerY())
                    mapRect(resultRect)
                }
            }

            offsetXY?.let {
                Matrix().apply {
                    setTranslate(it.width, it.height)
                    mapRect(resultRect)
                }
            }

            degree?.let {
                Matrix().apply {
                    setRotate(it, resultRect.centerX(), resultRect.centerY())
                    mapRect(resultRect)
                }
            }

            return resultRect
        }

        fun transformToPolygon(
            rect: Rect,
            offsetXY: SizeF?,
            scaleXY: SizeF?,
            degree: Float?
        ): Polygon {
            val resultRect = RectF(rect)

            scaleXY?.let {
                Matrix().apply {
                    setScale(it.width, it.height, resultRect.centerX(), resultRect.centerY())
                    mapRect(resultRect)
                }
            }

            offsetXY?.let {
                Matrix().apply {
                    setTranslate(it.width, it.height)
                    mapRect(resultRect)
                }
            }

            degree?.let {
                val resultPoints = ArrayList<PointF>()

                val pivot = PointF(resultRect.centerX(), resultRect.centerY())
                val pLT = floatArrayOf(resultRect.left, resultRect.top)
                Matrix().apply {
                    setRotate(it, pivot.x, pivot.y)
                    mapPoints(pLT)
                }
                resultPoints.add(PointF(pLT[0], pLT[1]))

                val pRT = floatArrayOf(resultRect.right, resultRect.top)
                Matrix().apply {
                    setRotate(it, pivot.x, pivot.y)
                    mapPoints(pRT)
                }
                resultPoints.add(PointF(pRT[0], pRT[1]))

                val pRB = floatArrayOf(resultRect.right, resultRect.bottom)
                Matrix().apply {
                    setRotate(it, pivot.x, pivot.y)
                    mapPoints(pRB)
                }
                resultPoints.add(PointF(pRB[0], pRB[1]))

                val pLB = floatArrayOf(resultRect.left, resultRect.bottom)
                Matrix().apply {
                    setRotate(it, pivot.x, pivot.y)
                    mapPoints(pLB)
                }
                resultPoints.add(PointF(pLB[0], pLB[1]))

                return Polygon(resultPoints)
            }
            return Polygon(resultRect)
        }
    }
}