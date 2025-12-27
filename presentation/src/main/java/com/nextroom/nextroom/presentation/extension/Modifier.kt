package com.nextroom.nextroom.presentation.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Modifier.throttleClick(
    throttleDelayMillis: Long = 400L,
    onClick: () -> Unit
): Modifier {
    val lastClickTime = remember { mutableLongStateOf(0L) }
    return clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime.longValue > throttleDelayMillis) {
            lastClickTime.longValue = currentTime
            onClick()
        }
    }
}