package com.nextroom.nextroom.presentation.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.nextroom.nextroom.presentation.R

class PainterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var tool = PEN
    private val paths: MutableList<Path> = mutableListOf()
    private val paint: Paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = PEN_SIZE
        isAntiAlias = true
    }
    private var path: Path? = null

    private var listener: PainterListener? = null

    init {
        setBackgroundColor(resources.getColor(R.color.Dark01, null))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paths.forEach { canvas.drawPath(it, paint) }
    }

    fun setOnPainterListener(listener: PainterListener) {
        this.listener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount == 1) {
            val x = event.x
            val y = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (tool == PEN) {
                        path = Path().apply {
                            moveTo(x, y)
                            paths.add(this)
                            listener?.onPainted(paths)
                        }
                    } else {
                        erase(x, y)
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (tool == PEN) {
                        path?.lineTo(x, y)
                    } else {
                        erase(x, y)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    path?.let {
                        paths.add(it)
                        listener?.onPainted(paths)
                    }
                    path = null
                }

                else -> return false
            }
            invalidate()
        } else if (event.pointerCount == 2) {
            return false
        }

        return true
    }

    fun pickPen() {
        tool = PEN
    }

    fun pickEraser() {
        tool = ERASER
    }

    fun eraseAll() {
        paths.clear()
        listener?.onPainted(paths)
        invalidate()
    }

    private fun erase(x: Float, y: Float) {
        paths.removeIf {
            val bounds = RectF()
            it.computeBounds(bounds, true)
            bounds.contains(x, y)
        }
        listener?.onPainted(paths)
    }

    fun setPaths(paths: List<Path>) {
        this.paths.clear()
        this.paths.addAll(paths)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.listener = null
    }

    companion object {
        private const val ERASER = 0
        private const val PEN = 1

        private const val PEN_SIZE = 5f
    }
}

fun interface PainterListener {
    fun onPainted(paths: List<Path>)
}
