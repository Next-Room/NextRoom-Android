package com.nextroom.nextroom.presentation.extension

import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

val Activity.windowInsetsController: WindowInsetsControllerCompat
    get() = WindowCompat.getInsetsController(window, window.decorView)

fun Activity.enableFullScreen(
    hideStatusBar: Boolean = true,
    hideNavigationBar: Boolean = true,
) {
    when {
        hideStatusBar && hideNavigationBar -> WindowInsetsCompat.Type.systemBars()
        hideStatusBar -> WindowInsetsCompat.Type.statusBars()
        hideNavigationBar -> WindowInsetsCompat.Type.navigationBars()
        else -> null
    }?.let { types ->
        windowInsetsController.apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(types)
        }
    } ?: disableFullScreen()
}

fun Activity.disableFullScreen() {
    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
}
