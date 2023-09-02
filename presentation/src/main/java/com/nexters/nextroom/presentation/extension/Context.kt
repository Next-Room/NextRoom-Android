package com.nexters.nextroom.presentation.extension

import android.content.Context
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

val Context.inputMethodManager
    get() = ContextCompat.getSystemService(this, InputMethodManager::class.java)

val Context.statusBarHeight: Int
    get() = run {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

val Context.navigationHeight: Int
    get() = run {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

val Context.vibrator: Vibrator
    get() = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
