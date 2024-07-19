package com.nextroom.nextroom.presentation.util

import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

interface WindowInsetsManager {
    fun setActivity(activity: ComponentActivity)

    fun enableFullScreen()
    fun enableFullScreen(hideStatusBar: Boolean, hideNavigationBar: Boolean)

    fun disableFullScreen()
    fun disableFullScreen(showStatusBar: Boolean, showNavigationBar: Boolean)
}

class WindowInsetsManagerImpl : WindowInsetsManager {
    private var _activity: ComponentActivity? = null
    private val activity: ComponentActivity
        get() = _activity
            ?: error("WindowInsetsManager에 activity 가 설정되지 않았습니다. setActivity()")

    private val windowInsetsController
        get() = WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    private val _event = Channel<WindowInsetsEvent>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun setActivity(activity: ComponentActivity) {
        _activity = activity
        setOnApplyWindowInsetsListener()
    }

    override fun enableFullScreen() {
        enableFullScreen(hideStatusBar = true, hideNavigationBar = true)
    }

    override fun enableFullScreen(hideStatusBar: Boolean, hideNavigationBar: Boolean) {
        event(WindowInsetsEvent.Hide(statusBar = hideStatusBar, navigationBar = hideNavigationBar))
    }

    override fun disableFullScreen() {
        disableFullScreen(showStatusBar = true, showNavigationBar = true)
    }

    override fun disableFullScreen(showStatusBar: Boolean, showNavigationBar: Boolean) {
        event(WindowInsetsEvent.Show(statusBar = showStatusBar, navigationBar = showNavigationBar))
    }

    private fun setOnApplyWindowInsetsListener() {
        ViewCompat.setOnApplyWindowInsetsListener(activity.window.decorView) { view, windowInsets ->
            activity.lifecycleScope.launch {
                _event.receiveAsFlow().collect(::handleEvent)
            }

            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }
    }

    private fun handleEvent(event: WindowInsetsEvent) {
        val type = when {
            event.statusBar && event.navigationBar -> WindowInsetsCompat.Type.systemBars()
            event.statusBar -> WindowInsetsCompat.Type.statusBars()
            event.navigationBar -> WindowInsetsCompat.Type.navigationBars()
            else -> return
        }
        when (event) {
            is WindowInsetsEvent.Hide -> windowInsetsController.hide(type)
            is WindowInsetsEvent.Show -> windowInsetsController.show(type)
        }
    }

    private fun event(event: WindowInsetsEvent) {
        activity.lifecycleScope.launch {
            _event.send(event)
        }
    }
}

private sealed interface WindowInsetsEvent {
    val statusBar: Boolean
    val navigationBar: Boolean

    data class Show(
        override val statusBar: Boolean,
        override val navigationBar: Boolean
    ) : WindowInsetsEvent

    data class Hide(
        override val statusBar: Boolean,
        override val navigationBar: Boolean
    ) : WindowInsetsEvent
}
