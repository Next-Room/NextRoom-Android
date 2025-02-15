package com.nextroom.nextroom.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.nextroom.nextroom.presentation.R

class NRLoading @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        // 레이아웃의 크기를 부모의 크기로 설정
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )

        // 속성에서 배경 타입 설정
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NRLoading)
        val backgroundTypeOrdinal =
            typedArray.getInt(R.styleable.NRLoading_backgroundType, BackgroundType.TRANSPARENT.ordinal)
        typedArray.recycle()

        setBackgroundType(BackgroundType.entries[backgroundTypeOrdinal])

        // ProgressBar를 가운데에 배치
        val progressBar = ProgressBar(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
            ).apply {
                // 가운데 정렬
                gravity = android.view.Gravity.CENTER
            }
        }

        // 로딩중에 하위 뷰 터치 막기
        setOnTouchListener { _, _ ->
            true
        }

        // ProgressBar를 이 뷰에 추가
        addView(progressBar)
    }

    fun setBackgroundType(backgroundType: BackgroundType) {
        ContextCompat.getColor(context, backgroundType.colorRes).also {
            setBackgroundColor(it)
        }
    }

    enum class BackgroundType(@ColorRes val colorRes: Int) {
        TRANSPARENT(android.R.color.transparent),
        BLACK(android.R.color.black),
    }
}
