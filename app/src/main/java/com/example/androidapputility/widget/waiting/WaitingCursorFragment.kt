package com.example.androidapputility.widget.waiting

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.example.androidapputility.BaseFragment
import com.example.androidapputility.R

class WaitingCursorFragment : BaseFragment() {

    companion object {
    }

    private lateinit var ivWaitingCursor: ImageView
    private var useAnimator = true
    private var animator: Animator? = null

    private var waitingCursorDrawableId: Int? = null
    private var animationDrawable: AnimationDrawable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_waiting_cursor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivWaitingCursor = view.findViewById(R.id.iv_waiting_cursor)
        startWaitingAnimation()

        val clickToClose = false // Enable to test
        if (clickToClose) {
            ivWaitingCursor.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    dismiss()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopWaitingAnimation()
    }

    fun setWaitingCursor(drawableId: Int) {
        waitingCursorDrawableId = drawableId
    }

    fun dismiss() {
        stopWaitingAnimation()
        close()
    }

    private fun startWaitingAnimation() {
        ivWaitingCursor.visibility = View.VISIBLE
        if (waitingCursorDrawableId != null) {
            ivWaitingCursor.setBackgroundResource(waitingCursorDrawableId!!)
            animationDrawable = ivWaitingCursor.background as AnimationDrawable
            animationDrawable?.start()
        } else {
            if (useAnimator) {
                animator = ObjectAnimator.ofFloat(ivWaitingCursor, "rotation", 0f, 360f).apply {
                    duration = 1000
                    repeatCount = ValueAnimator.INFINITE
                    interpolator = LinearInterpolator()
                }
                animator?.start()
            } else {
                val rotateAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.rotate)
                ivWaitingCursor.startAnimation(rotateAnimation)
            }
        }
    }

    private fun stopWaitingAnimation() {
        animationDrawable?.let {
            it.stop()
        } ?: {
            if (useAnimator) {
                animator?.cancel()
            } else {
                ivWaitingCursor.clearAnimation()
            }
        }
        ivWaitingCursor.visibility = View.GONE
    }
}