package com.example.androidapputility.widget

import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.toPointF
import androidx.core.graphics.toRectF

class Polygon {
    private val points: Array<PointF>

    constructor(p: Array<PointF>) {
        points = p
    }

    constructor(p: List<PointF>) {
        points = p.toTypedArray()
    }

    constructor(r: RectF) {
        points = Array<PointF>(4) { PointF() }
        points[0] = PointF(r.left, r.top)
        points[1] = PointF(r.right, r.top)
        points[2] = PointF(r.right, r.bottom)
        points[3] = PointF(r.left, r.bottom)
    }

    private fun indices(): IntRange {
        return points.indices
    }

    fun size(): Int {
        return points.size
    }

    operator fun get(index: Int): PointF {
        return points[index]
    }

    fun getCenter(): PointF {
        var sumX = 0.0f
        var sumY = 0.0f
        for (p in points) {
            sumX += p.x
            sumY += p.y
        }

        val num = points.size
        return PointF(sumX / num, sumY / num)
    }

    fun getMaxRectInside(): RectF? {
        val tolerancePixels = 0
        var maxRect: RectF? = null
        var maxArea = 0.0f

        // Brute-force search: Iterate over all pairs of points in the polygon
        for (i in points.indices) {
            for (j in i + 1 until points.size) {
                val p1 = points[i]
                val p2 = points[j]

                // Create potential rectangle aligned with the axes
                val leftTop = PointF(minOf(p1.x, p2.x), minOf(p1.y, p2.y))
                val rightBottom = PointF(maxOf(p1.x, p2.x), maxOf(p1.y, p2.y))
                val candidateRect = RectF(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y)

                // Check if the rectangle is fully inside the polygon
                val rectangleCorners = listOf(
                    PointF(leftTop.x + tolerancePixels, leftTop.y + tolerancePixels),
                    PointF(rightBottom.x - tolerancePixels, leftTop.y + tolerancePixels),
                    PointF(rightBottom.x - tolerancePixels, rightBottom.y - tolerancePixels),
                    PointF(leftTop.x + tolerancePixels, rightBottom.y - tolerancePixels)
                )

                if (rectangleCorners.all { include(it) }) {
                    val area = 1.0f * candidateRect.width() * candidateRect.height()
                    if (area > maxArea) {
                        maxArea = area
                        maxRect = candidateRect
                    }
                }
            }
        }
        return maxRect
    }

    fun getBoundingRect(): RectF {
        val rect = RectF(Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE)
        for (p in points) {
            rect.left = Math.min(rect.left, p.x)
            rect.top = Math.min(rect.top, p.y)
            rect.right = Math.max(rect.right, p.x)
            rect.bottom = Math.max(rect.bottom, p.y)
        }
        return rect
    }

    fun include(p: Point): Boolean {
        return include(p.toPointF())
    }

    fun include(p: PointF): Boolean {
        // Check by Ray-Casting Algorithm
        var inside = false
        for (i in points.indices) {
            val j = (i + 1) % points.size
            val p1 = points[i]
            val p2 = points[j]
            if ((p1.y > p.y) != (p2.y > p.y) &&
                (p.x < (p2.x - p1.x) * (p.y - p1.y) / (p2.y - p1.y) + p1.x)
            ) {
                inside = !inside
            }
        }
        return inside
    }

    fun include(r: Rect): Boolean {
        return include(r.toRectF())
    }

    fun include(r: RectF): Boolean {
        val tolerancePixels = 0

        val rectPoints = Array<PointF>(4) { PointF() }
        rectPoints[0].x = r.left + tolerancePixels
        rectPoints[0].y = r.top + tolerancePixels
        rectPoints[1].x = r.right - tolerancePixels
        rectPoints[1].y = r.top + tolerancePixels
        rectPoints[2].x = r.right - tolerancePixels
        rectPoints[2].y = r.bottom - tolerancePixels
        rectPoints[3].x = r.left + tolerancePixels
        rectPoints[3].y = r.bottom - tolerancePixels

        for (i in rectPoints.indices) {
            if (!include(rectPoints[i]))
                return false
        }
        return true
    }

    fun intersect(polygon: Polygon): Polygon {
        fun crossProduct(o: PointF, a: PointF, b: PointF): Float {
            return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x)
        }

        fun intersect(p1: PointF, p2: PointF, p3: PointF, p4: PointF): PointF? {
            val a1 = p2.y - p1.y
            val b1 = p1.x - p2.x
            val c1 = a1 * p1.x + b1 * p1.y

            val a2 = p4.y - p3.y
            val b2 = p3.x - p4.x
            val c2 = a2 * p3.x + b2 * p3.y

            val determinant = a1 * b2 - a2 * b1
            if (determinant == 0.0f) {
                return null // Lines are parallel
            }

            val x = (b2 * c1 - b1 * c2) / determinant
            val y = (a1 * c2 - a2 * c1) / determinant

            return PointF(x, y)
        }

        fun isInside(point: PointF, edgeStart: PointF, edgeEnd: PointF): Boolean {
            return crossProduct(edgeStart, edgeEnd, point) >= 0
        }

        var resultPoints = Array<PointF>(points.size) { PointF() }
        for (i in points.indices) {
            resultPoints[i].x = points[i].x
            resultPoints[i].y = points[i].y
        }
        var resultPointsList = resultPoints.toList()

        val clippedPolygon = Array<PointF>(polygon.size()) { PointF() }
        for (i in polygon.indices()) {
            clippedPolygon[i].x = polygon[i].x
            clippedPolygon[i].y = polygon[i].y
        }

        for (i in clippedPolygon.indices) {
            val clipEdgeStart = clippedPolygon[i]
            val clipEdgeEnd = clippedPolygon[(i + 1) % clippedPolygon.size]
            val inputList = resultPointsList
            resultPointsList = mutableListOf()

            for (j in inputList.indices) {
                val subjectEdgeStart = inputList[j]
                val subjectEdgeEnd = inputList[(j + 1) % inputList.size]

                if (isInside(subjectEdgeEnd, clipEdgeStart, clipEdgeEnd)) {
                    if (!isInside(subjectEdgeStart, clipEdgeStart, clipEdgeEnd)) {
                        intersect(
                            subjectEdgeStart,
                            subjectEdgeEnd,
                            clipEdgeStart,
                            clipEdgeEnd
                        )?.let {
                            resultPointsList.add(it)
                        }
                    }
                    resultPointsList.add(subjectEdgeEnd)
                } else if (isInside(subjectEdgeStart, clipEdgeStart, clipEdgeEnd)) {
                    intersect(subjectEdgeStart, subjectEdgeEnd, clipEdgeStart, clipEdgeEnd)?.let {
                        resultPointsList.add(it)
                    }
                }
            }
        }
        return Polygon(resultPointsList)
    }

    fun translate(dx: Float, dy: Float): Polygon {
        val resultPoints = Array<PointF>(points.size) { PointF() }
        for ((idx, p) in points.withIndex()) {
            resultPoints[idx].x = p.x + dx
            resultPoints[idx].y = p.y + dy
        }
        return Polygon(resultPoints)
    }

    fun scale(pivot: PointF, scaleX: Float, scaleY: Float): Polygon {
        val resultPoints = Array<PointF>(points.size) { PointF() }
        for ((idx, p) in points.withIndex()) {
            // Translate point to origin
            val translatedX = p.x - pivot.x
            val translatedY = p.y - pivot.y

            // Apply scaling
            val scaledX = translatedX * scaleX
            val scaledY = translatedY * scaleY

            // Translate point back
            resultPoints[idx] = PointF(scaledX + pivot.x, scaledY + pivot.y)
        }
        return Polygon(resultPoints)
    }

    fun rotate(pivot: PointF, degree: Float): Polygon {
        val resultPoints = Array<PointF>(points.size) { PointF() }
        for ((idx, p) in points.withIndex()) {
            resultPoints[idx] = Point2D.rotate(p, pivot, degree)
        }
        return Polygon(resultPoints)
    }

    fun transform() {

    }

    fun vFlip(pivot: PointF): Polygon {
        return Polygon(points.toList().map { p ->
            PointF(p.x, 2 * pivot.y - p.y)
        }.toTypedArray())
    }

    fun hFlip(pivot: PointF): Polygon {
        val resultPoints = Array<PointF>(points.size) { PointF() }
        for (i in points.indices) {
            val translatedX = points[i].x - pivot.x
            val translatedY = points[i].y - pivot.y

            // Perform the horizontal flip
            val flippedX = -translatedX
            val flippedY = translatedY

            // Translate the point back to its original position
            val horiFlipX = flippedX + pivot.x
            val horiFlipY = flippedY + pivot.y
            resultPoints[i] = PointF(horiFlipX, horiFlipY)
        }

        return Polygon(resultPoints)
    }

    fun isPassed(p: PointF): Boolean {
        for (i in points.indices) {
            val edge = Line(points[i], points[(i + 1) % points.size])
            if (edge.isPassed(p))
                return true
        }
        return false
    }
}