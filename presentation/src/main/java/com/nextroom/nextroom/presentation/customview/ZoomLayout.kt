package com.nextroom.nextroom.presentation.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.View
import android.widget.FrameLayout
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign


class ZoomLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle), OnScaleGestureListener {

    private var scale = 1.0f
    private var lastScaleFactor = 0f
    private val scaleDetector by lazy { ScaleGestureDetector(context, this) }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(ev)
        applyScaleAndTranslation()
        return false
    }

    override fun onScale(scaleDetector: ScaleGestureDetector): Boolean {
        val scaleFactor = scaleDetector.scaleFactor
        if (lastScaleFactor == 0f || (sign(scaleFactor.toDouble()) == sign(lastScaleFactor.toDouble()))) {
            scale *= scaleFactor
            scale = max(MIN_ZOOM.toDouble(), min(scale.toDouble(), MAX_ZOOM.toDouble())).toFloat()
            lastScaleFactor = scaleFactor
        } else {
            lastScaleFactor = 0f
        }

        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(scaleDetector: ScaleGestureDetector) {}

    private fun applyScaleAndTranslation() {
        child().scaleX = scale
        child().scaleY = scale
    }

    private fun child(): View {
        return getChildAt(0)
    }

    companion object {
        private const val MIN_ZOOM = 1.0f
        private const val MAX_ZOOM = 8.0f
    }
}