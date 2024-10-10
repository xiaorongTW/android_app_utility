package com.example.androidapputility.utility

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Size
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.SeekBar
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

        fun updateWidth(view: View, width: Int) {
            updateSize(view, width, null)
        }

        fun updateHeight(view: View, height: Int) {
            updateSize(view, null, height)
        }

        fun updateSize(view: View, width: Int?, height: Int?) {
            val params = view.layoutParams
            width?.let {
                params.width = it
            }
            height?.let {
                params.height = it
            }
            view.layoutParams = params
        }

        fun updateMargin(view: View, left: Int?, top: Int?, right: Int?, bottom: Int?) {
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

        fun setGlobalLayoutListener(view: View, listener: ViewTreeObserver.OnGlobalLayoutListener) {
            if (view.viewTreeObserver.isAlive) {
                view.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        listener.onGlobalLayout()
                        view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }

        fun performSnapHapticFeedback(v: View) {
            v.performHapticFeedback(
                HapticFeedbackConstants.LONG_PRESS,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
        }

        fun blur(view: View, blurRadius: Float) { // range: 0 ~ 25
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val blurEffect =
                    RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP)
                view.setRenderEffect(blurEffect)
            } else {
                val viewBitmap =
                    Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(viewBitmap)
                view.draw(canvas)

                val renderScript = RenderScript.create(view.context)
                val input = Allocation.createFromBitmap(renderScript, viewBitmap)
                val output = Allocation.createTyped(renderScript, input.type)
                val scriptIntrinsicBlur =
                    ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

                scriptIntrinsicBlur.setRadius(blurRadius)
                scriptIntrinsicBlur.setInput(input)
                scriptIntrinsicBlur.forEach(output)
                output.copyTo(viewBitmap)

                view.background = BitmapDrawable(view.context.resources, viewBitmap)
                renderScript.destroy()
            }
        }

        fun blurBackground(view: View, blurRadius: Float) { // range: 0 ~ 25
            val background = view.background
            if (background == null || background is ColorDrawable) {
                val rootView = view.rootView
                rootView.isDrawingCacheEnabled = true
                val bitmap = Bitmap.createBitmap(rootView.drawingCache)
                rootView.isDrawingCacheEnabled = false

                val blurredBitmap = BitmapUtil.blurBitmap(view.context, bitmap, blurRadius)
                view.background = BitmapDrawable(view.context.resources, blurredBitmap)
            } else {
                val viewBitmap =
                    Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(viewBitmap)
                view.background.setBounds(0, 0, view.width, view.height)
                view.background.draw(canvas)

                val renderScript = RenderScript.create(view.context)
                val input = Allocation.createFromBitmap(renderScript, viewBitmap)
                val output = Allocation.createTyped(renderScript, input.type)
                val scriptIntrinsicBlur =
                    ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

                scriptIntrinsicBlur.setRadius(blurRadius)
                scriptIntrinsicBlur.setInput(input)
                scriptIntrinsicBlur.forEach(output)
                output.copyTo(viewBitmap)

                view.background = BitmapDrawable(view.context.resources, viewBitmap)
                renderScript.destroy()
            }
        }

        fun getSeekbarThumbScreenRect(s: SeekBar): Rect {
            val thumb = s.thumb
            val bounds = thumb.bounds

            val pos = IntArray(2)
            s.getLocationOnScreen(pos)

            val left = pos[0] + bounds.left
            val top = pos[1] + bounds.top
            return Rect(left, top, left + bounds.width(), top + bounds.height())
        }
    }
}