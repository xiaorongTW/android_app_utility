package com.example.androidapputility.testbed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.androidapputility.BaseFragment
import com.example.androidapputility.R
import com.example.androidapputility.utility.ViewUtil
import com.example.androidapputility.widget.DegreeRulerView

class DegreeRulerFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_degree_ruler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val degreeRulerScrollerView =
            view.findViewById<HorizontalScrollView>(R.id.degree_ruler_scroller)
        val degreeRulerView = view.findViewById<DegreeRulerView>(R.id.degree_ruler_view)
        val degreePointerView = view.findViewById<View>(R.id.degree_pointer_view)
        val degreePointerTextView = view.findViewById<TextView>(R.id.degree_pointer_text_view)

        val scrolledDegreeRange = Pair<Float, Float>(-45.0f, 45.0f)
        val degreeRange = 45
        val pixelsPerDegree = 25

        degreeRulerScrollerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val degreeHorizontalScrollViewW = degreeRulerScrollerView.measuredWidth
                degreeRulerView.setupParameters(
                    degreeRange,
                    pixelsPerDegree,
                    degreeHorizontalScrollViewW / 2
                )
                degreeRulerView.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        degreeRulerScrollerView.scrollX = degreeRulerView.getScrollXByDegree(0f)

                        val marginOfDegreePointerView =
                            2 * degreeRulerScrollerView.measuredHeight / 5
                        (degreePointerView.layoutParams as RelativeLayout.LayoutParams).topMargin =
                            marginOfDegreePointerView
                        degreePointerView.requestLayout()

                        val marginOfDegreePointerTextView =
                            -20 + (2 * degreeRulerScrollerView.measuredHeight / 5 - degreePointerTextView.height) // Fine tune for degreePointerTextView
                        degreePointerTextView.textSize = DegreeRulerView.DEGREE_TEXT_SIZE
                        (degreePointerTextView.layoutParams as RelativeLayout.LayoutParams).topMargin =
                            marginOfDegreePointerTextView
                        degreePointerTextView.requestLayout()

                        degreeRulerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
                degreeRulerScrollerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        degreeRulerScrollerView.setOnScrollChangeListener(object : View.OnScrollChangeListener {
            private var needSnapHapticFeedback = false
            private var scrolledDegree = 0.0f
            private var degreeScrolledX = -1

            override fun onScrollChange(
                v: View?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (degreeScrolledX == -1)
                    degreeScrolledX = scrollX

                var degree = degreeRulerView.getScrolledDegree(scrollX)

                val thresholdRatio = 0.4f
                val diffScrollX = scrollX - oldScrollX
                if (diffScrollX > 0
                    && (scrollX - degreeScrolledX) >= thresholdRatio * pixelsPerDegree
                    && (scrollX - degreeScrolledX) <= pixelsPerDegree
                ) {
                    val targetDegree = scrolledDegree + 1
                    degree = Math.min(targetDegree, scrolledDegreeRange.second)
                    degreeRulerScrollerView.scrollX = degreeRulerView.getScrollXByDegree(degree)
                    needSnapHapticFeedback = true
                } else if (diffScrollX < 0
                    && (degreeScrolledX - scrollX) >= thresholdRatio * pixelsPerDegree
                    && (degreeScrolledX - scrollX) <= pixelsPerDegree
                ) {
                    val targetDegree = scrolledDegree - 1
                    degree = Math.max(targetDegree, scrolledDegreeRange.first)
                    degreeRulerScrollerView.scrollX = degreeRulerView.getScrollXByDegree(degree)
                    needSnapHapticFeedback = true
                } else if (Math.abs(scrollX - degreeScrolledX) in 1 until pixelsPerDegree) {
                    // Skip handling for sensitive degree scrolling
                    return
                }

                degreePointerTextView.text =
                    degree.toInt().toString() + DegreeRulerView.DEGREE_SYMBOL
                if (scrolledDegree.toInt() == degree.toInt())
                    return

                degreeScrolledX = scrollX
                scrolledDegree = degree.toInt().toFloat()
                if (scrolledDegree.toInt() % 15 == 0)
                    needSnapHapticFeedback = true

                if (needSnapHapticFeedback) {
                    ViewUtil.performSnapHapticFeedback(degreeRulerView)
                    needSnapHapticFeedback = false
                }
            }
        })
    }
}