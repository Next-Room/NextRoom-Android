package com.nextroom.nextroom.presentation.extension

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.postDelayed
import androidx.core.view.updateLayoutParams

fun View.showKeyboard(): Boolean = run {
    requestFocus()
    context.inputMethodManager?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT) ?: false
}

fun View.hideKeyboard(): Boolean =
    context.inputMethodManager?.hideSoftInputFromWindow(windowToken, 0) ?: false

fun View.setOnLongClickListener(duration: Long = 2000L, action: () -> Unit) {
    var runnable: Runnable? = null
    setOnTouchListener { _, event ->
        return@setOnTouchListener when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                runnable?.let {
                    removeCallbacks(it)
                    return@setOnTouchListener false
                }
                runnable = postDelayed(duration) {
                    if (runnable != null) {
                        performClick()
                        action()
                    }
                }
                true
            }

            MotionEvent.ACTION_UP -> {
                removeCallbacks(runnable)
                runnable = null
                false
            }

            else -> false
        }
    }
}

fun View.addMargin(left: Int = 0, right: Int = 0, top: Int = 0, bottom: Int = 0) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        setMargins(leftMargin + left, topMargin + top, rightMargin + right, bottomMargin + bottom)
    }
}
