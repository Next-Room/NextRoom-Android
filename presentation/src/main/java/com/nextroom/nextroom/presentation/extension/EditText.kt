package com.nextroom.nextroom.presentation.extension

import android.view.MotionEvent
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.doAfterTextChanged
import com.nextroom.nextroom.presentation.R

fun EditText.setLineColor(@ColorRes color: Int) {
    val wrappedDrawable = DrawableCompat.wrap(background)
    DrawableCompat.setTint(wrappedDrawable.mutate(), resources.getColor(color, null))
    setBackgroundDrawable(wrappedDrawable)
}

fun EditText.setError() {
    setLineColor(R.color.Red)
}

fun EditText.setStateListener() {
    doAfterTextChanged {
        when {
            hasFocus() || it?.isNotBlank() == true -> {
                setTextColor(resources.getColor(R.color.White, null))
            }
            else -> setTextColor(resources.getColor(R.color.Gray01, null))
        }
    }
    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                setLineColor(R.color.White)
            }
            else -> setLineColor(R.color.Gray01)
        }
        return@setOnTouchListener false
    }
}
