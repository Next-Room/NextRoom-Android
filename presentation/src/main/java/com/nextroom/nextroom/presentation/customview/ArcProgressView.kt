package com.nextroom.nextroom.presentation.customview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.extension.dp
import kotlin.math.cos
import kotlin.math.sin

class ArcProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private var paint: Paint? = null
    private var handlePaint: Paint? = null
    private var textPaint: Paint? = null
    private val rectF = RectF()

    private var strokeWidth = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var progressTextSize = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var progressTextColor = 0
        set(value) {
            field = value
            invalidate()
        }

    private var finishedStrokeColor = 0
        set(value) {
            field = value
            invalidate()
        }
    private var unfinishedStrokeColor = 0
        set(value) {
            field = value
            invalidate()
        }
    private var arcAngle = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val defaultSize = 200.dp
    private val minSize = 100.dp
    private val defaultUnfinishedColor = ContextCompat.getColor(context, R.color.Gray01)
    private val defaultFinishedColor = ContextCompat.getColor(context, R.color.White)
    private val defaultTextColor = ContextCompat.getColor(context, R.color.Gray04)
    private val defaultStrokeWidth: Float = 6.dp.toFloat()
    private val defaultTextSize: Float = 56.dp.toFloat()

    var timeLimit = 3600
        set(value) {
            if (value > 0) {
                field = value
                invalidate()
            }
        }
    var lastSeconds: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    init {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, 0)
        initAttrs(attributes)
        initPainters()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val radius = rectF.width() / 2
        val startAngle = 270 - arcAngle / 2F
        val endAngle = 270 + arcAngle / 2F
        val progress = lastSeconds.toFloat() / timeLimit
        val sweepStartAngle = startAngle + arcAngle * (1 - progress)

        // 트랙 그리기
        paint?.apply {
            color = unfinishedStrokeColor
            canvas.drawArc(rectF, startAngle, arcAngle, false, this)
        }
        // 남은 시간 그리기
        paint?.apply {
            color = finishedStrokeColor
            if (timeLimit > 0) {
                // 트랙
                canvas.drawArc(rectF, sweepStartAngle, arcAngle * progress, false, this)
            }
        }
        // 핸들
        handlePaint?.apply {
            color = finishedStrokeColor
            val angle = Math.toRadians(360 - sweepStartAngle.toDouble()).toFloat()
            val handleX = rectF.centerX() + cos(angle) * radius
            val handleY = rectF.centerY() - sin(angle) * radius
            canvas.drawCircle(handleX, handleY, 20F, this)
        }
        // Start & End 글자
        textPaint?.apply {
            color = progressTextColor
            textSize = 14.dp.toFloat()
            try {
                typeface = Typeface.defaultFromStyle(R.style.Poppins_14)
            } catch (_: ArrayIndexOutOfBoundsException) {
            }

            val edgeStartAngle = Math.toRadians(startAngle.toDouble()).toFloat()
            val edgeEndAngle = Math.toRadians(endAngle.toDouble()).toFloat()
            val edgeTextY = rectF.centerY() + sin(edgeStartAngle) * radius + 30
            val edgeStartX = rectF.centerX() + cos(edgeStartAngle) * radius
            val edgeEndX = rectF.centerX() + cos(edgeEndAngle) * radius
            canvas.drawText(
                "Start",
                edgeStartX + 30,
                edgeTextY,
                this,
            ) // TODO Hard coding refactoring
            canvas.drawText("End", edgeEndX - 120, edgeTextY, this) // TODO Hard coding refactoring
        }

        /*val text = lastSeconds.toTimerFormat()

        if (text.isNotBlank()) {
            textPaint?.let {
                it.color = progressTextColor
                it.textSize = progressTextSize
                try {
                    it.typeface = Typeface.defaultFromStyle(R.style.Poppins_14)
                } catch (_: ArrayIndexOutOfBoundsException) {
                }
                val textHeight = it.descent() + it.ascent()
                val textBaseline = (height - textHeight) / 2.0f - 26.dp

                canvas.drawText(
                    text,
                    (width - it.measureText(text)) / 2.0f,
                    textBaseline,
                    it,
                )
            }
        }*/
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)

        when {
            // Sets the default value when the layout parameter is set to wrap_content
            layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT && layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT -> {
                setMeasuredDimension(defaultSize, defaultSize)
                heightSize = defaultSize
                widthSize = heightSize
            }

            // When any layout parameter of width/height is = wrap_content, the default value is set
            layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT -> {
                setMeasuredDimension(defaultSize, heightSize)
                widthSize = defaultSize
            }

            layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT -> {
                setMeasuredDimension(widthSize, defaultSize)
                heightSize = defaultSize
            }

            else -> {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
        }
        rectF[strokeWidth / 2f + 12, strokeWidth / 2f + 12, widthSize - strokeWidth / 2f - 12] =
            heightSize - strokeWidth / 2f
    }

    private fun initAttrs(attributes: TypedArray) {
        try {
            finishedStrokeColor = attributes.getColor(
                R.styleable.ArcProgress_arc_finished_color,
                defaultFinishedColor,
            )
            unfinishedStrokeColor = attributes.getColor(
                R.styleable.ArcProgress_arc_unfinished_color,
                defaultUnfinishedColor,
            )

            progressTextColor = attributes.getColor(
                R.styleable.ArcProgress_arc_progress_text_color,
                defaultTextColor,
            )
            progressTextSize = attributes.getDimension(
                R.styleable.ArcProgress_arc_progress_text_size,
                defaultTextSize,
            )

            lastSeconds = attributes.getInt(R.styleable.ArcProgress_lastSeconds, 0)
            arcAngle = attributes.getFloat(
                R.styleable.ArcProgress_arc_angle,
                DEFAULT_ARC_ANGLE,
            )
            timeLimit = attributes.getInt(R.styleable.ArcProgress_timeLimit, DEFAULT_MAX)
            strokeWidth = attributes.getDimension(
                R.styleable.ArcProgress_arc_stroke_width,
                defaultStrokeWidth,
            )
        } finally {
            attributes.recycle()
        }
    }

    private var mValueAnimator: ValueAnimator? = null

    @JvmOverloads
    fun startAnim(duration: Int = DURATION) {
        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofInt(0, timeLimit).apply {
                addUpdateListener { animation ->
                    val animatorValue = animation.animatedValue as Int
                    lastSeconds = animatorValue
                }
            }
        }
        mValueAnimator?.let {
            it.duration = duration.toLong()
            it.start()
        }
    }

    private fun initPainters() {
        textPaint = TextPaint().apply {
            color = progressTextColor
            textSize = progressTextSize
            isAntiAlias = true
        }
        paint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            strokeWidth = this@ArcProgressView.strokeWidth
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        handlePaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    override fun invalidate() {
        initPainters()
        super.invalidate()
    }

    override fun getSuggestedMinimumHeight(): Int {
        return minSize
    }

    override fun getSuggestedMinimumWidth(): Int {
        return minSize
    }

    companion object {
        private const val DURATION = 500
        private const val DEFAULT_MAX = 100
        private const val DEFAULT_ARC_ANGLE = 270F
    }
}
