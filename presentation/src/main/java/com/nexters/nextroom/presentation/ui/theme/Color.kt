package com.nexters.nextroom.presentation.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Dark01 = Color(0xFF151516)
val Gray01 = Color(0xFF9898A0)
val Gray02 = Color(0xFF47474E)
val White = Color(0xFFFFFFFF)
val Red = Color(0xFFFF5065)

val NextRoomDarkColors = darkColorScheme(
    primary = White,
    onPrimary = Dark01,
    surfaceVariant = Gray02,
    surface = Dark01,
    onSurface = White,
    error = Red,
    onError = White
)

val NextRoomLightColors = lightColorScheme(
    primary = White,
    onPrimary = Dark01,
    surfaceVariant = Gray02,
    surface = Dark01,
    onSurface = White,
    error = Red,
    onError = White
)
