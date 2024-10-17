package com.nextroom.nextroom.presentation.customview

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.extension.dpToPx

class IndicatorDotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val dotList = mutableListOf<View>()

    init {
        orientation = HORIZONTAL
    }

    fun withViewPager(viewPager: ViewPager2) {
        initDotView(viewPager.adapter?.itemCount ?: 0)
    }

    fun select(index: Int) {
        dotList.mapIndexed { i, view ->
            (view.background as? GradientDrawable)?.setColor(
                if (index == i) ContextCompat.getColor(context, R.color.white)
                else ContextCompat.getColor(context, R.color.Gray05)
            )
        }
    }

    private fun initDotView(count: Int) {
        for (i in 0 until count) {
            val view = makeDotView()
            view.layoutParams = LayoutParams(8.dpToPx, 8.dpToPx).apply {
                marginStart = 2.dpToPx
                marginEnd = 2.dpToPx
            }
            dotList.add(view)
            addView(view)
        }
    }

    private fun makeDotView(): View =
        View(context).apply {
            background = ContextCompat.getDrawable(context, R.drawable.shape_dot_indicator)
        }
}