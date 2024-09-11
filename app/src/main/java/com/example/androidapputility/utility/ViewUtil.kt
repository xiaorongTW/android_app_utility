package com.example.androidapputility.utility

import android.content.Context
import android.graphics.Rect
import android.util.Size
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.RelativeLayout
import androidx.annotation.DimenRes
import java.lang.reflect.InvocationTargetException

class ViewUtil {

    companion object {

        fun getScreenRect(view: View): Rect {
            val pos = IntArray(2)
            view.getLocationOnScreen(pos)
            return Rect(pos[0], pos[1], pos[0] + view.width, pos[1] + view.height)
        }

        fun getMeasuredSize(view: View): Size {
            synchronized(view) {
                val w = View.MeasureSpec.makeMeasureSpec(
                    0,
                    View.MeasureSpec.UNSPECIFIED
                )
                val h = View.MeasureSpec.makeMeasureSpec(
                    0,
                    View.MeasureSpec.UNSPECIFIED
                )
                view.measure(w, h)
            }
            return Size(view.measuredWidth, view.measuredHeight)
        }

        fun updateLayoutWidth(view: View, width: Int) {
            updateLayoutSize(view, width, null)
        }

        fun updateLayoutHeight(view: View, height: Int) {
            updateLayoutSize(view, null, height)
        }

        fun updateLayoutSize(view: View, width: Int?, height: Int?) {
            val params = view.layoutParams
            width?.let {
                params.width = it
            }
            height?.let {
                params.height = it
            }
            view.layoutParams = params
        }

        fun updateLayoutMargin(view: View, left: Int?, top: Int?, right: Int?, bottom: Int?) {
            if (view !is RelativeLayout) {
                return
            }

            val params = view.getLayoutParams() as MarginLayoutParams
            left?.let {
                params.leftMargin = it
            }
            top?.let {
                params.topMargin = it
            }
            right?.let {
                params.rightMargin = it
            }
            bottom?.let {
                params.bottomMargin = it
            }
            view.setLayoutParams(params)
        }

        fun setTextSize(context: Context, obj: Any, @DimenRes id: Int) {
            setTextSize(obj, context.resources.getDimensionPixelSize(id).toFloat())
        }

        fun setTextSize(obj: Any, size: Float) {
            // Reference: https://stackoverflow.com/questions/160970/how-do-i-invoke-a-java-method-when-given-the-method-name-as-a-string
            try {
                val c: Class<*> = obj.javaClass
                val findMethod = c.getMethod(
                    "setTextSize",
                    Int::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType
                )
                if (findMethod != null) {
                    findMethod.invoke(obj, TypedValue.COMPLEX_UNIT_PX, size)
                }
            } catch (e: NoSuchMethodException) {
                println(e.toString())
            } catch (e: IllegalArgumentException) {
                println(e.toString())
            } catch (e: IllegalAccessException) {
                println(e.toString())
            } catch (e: InvocationTargetException) {
                println(e.toString())
            }
        }

        fun scaleTextSize(obj: Any, scale: Float) {
            try {
                val c: Class<*> = obj.javaClass
                val findMethod = c.getMethod("getTextSize")
                if (findMethod != null) {
                    val size = (findMethod.invoke(obj) as Float) * scale
                    setTextSize(obj, size)
                }
            } catch (e: NoSuchMethodException) {
                println(e.toString())
            } catch (e: java.lang.IllegalArgumentException) {
                println(e.toString())
            } catch (e: IllegalAccessException) {
                println(e.toString())
            } catch (e: InvocationTargetException) {
                println(e.toString())
            }
        }

        fun scaleTextSize(objectList: ArrayList<Any>, scale: Float) {
            for (obj in objectList) {
                try {
                    val c: Class<*> = obj.javaClass

                    // Skip handling for invisible object
                    var findMethod = c.getMethod("getVisibility") ?: continue
                    if (findMethod.invoke(obj) as Int != View.VISIBLE) continue

                    // Skip handling for view without textSize
                    findMethod = c.getMethod("getTextSize")
                    if (findMethod == null) continue

                    val textSize = findMethod.invoke(obj) as Float
                    if (textSize > 0.0f) {
                        setTextSize(obj, textSize * scale)
                    }
                } catch (e: NoSuchMethodException) {
                    println(e.toString())
                } catch (e: java.lang.IllegalArgumentException) {
                    println(e.toString())
                } catch (e: IllegalAccessException) {
                    println(e.toString())
                } catch (e: InvocationTargetException) {
                    println(e.toString())
                }
            }
        }

        fun performSnapHapticFeedback(v: View) {
            v.performHapticFeedback(
                HapticFeedbackConstants.LONG_PRESS,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
        }
    }
}