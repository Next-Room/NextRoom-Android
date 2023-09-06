package com.nexters.nextroom.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun NextRoomTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) NextRoomDarkColors else NextRoomLightColors,
        typography = NextRoomTypography,
        shapes = NextRoomShapes,
        content = content,
    )
}
