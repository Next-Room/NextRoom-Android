package com.nexters.nextroom.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nexters.nextroom.presentation.R

private val Poppins = FontFamily(
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semi_bold, FontWeight.SemiBold),
)

private val Pretendard = FontFamily(
    Font(R.font.pretendard_regular),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semi_bold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
)

val NextRoomTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Poppins,
        color = White,
        fontSize = 54.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    titleLarge = TextStyle(
        fontFamily = Pretendard,
        color = White,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
    ),
    titleMedium = TextStyle(
        fontFamily = Pretendard,
        color = White,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    bodyMedium = TextStyle(
        fontFamily = Pretendard,
        color = White,
        fontSize = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Poppins,
        color = White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
    ),
    labelMedium = TextStyle(
        fontFamily = Pretendard,
        color = Gray01,
        fontSize = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Pretendard,
        color = Gray01,
        fontSize = 12.sp,
    ),
)

val Typography.EngButton: TextStyle
    @Composable get() = labelMedium.copy(
        fontFamily = Poppins,
        color = Dark01,
        fontSize = 16.sp,
    )

val Typography.KorButton: TextStyle
    @Composable get() = labelMedium.copy(
        fontFamily = Pretendard,
        color = Dark01,
        fontSize = 16.sp,
    )

val Typography.EngSubButton: TextStyle
    @Composable get() = labelMedium.copy(
        fontFamily = Poppins,
        color = Dark01,
        fontSize = 14.sp,
    )

val Typography.KorSubButton: TextStyle
    @Composable get() = labelMedium.copy(
        fontFamily = Pretendard,
        color = Dark01,
        fontSize = 14.sp,
    )

val Typography.KeypadButton: TextStyle
    @Composable get() = labelMedium.copy(
        fontFamily = Poppins,
        color = White,
        fontSize = 24.sp,
    )

val Typography.ToolbarTimerTitle: TextStyle
    @Composable get() = bodyMedium.copy(
        fontFamily = Poppins,
        color = White,
        fontWeight = FontWeight.SemiBold,
    )

val Typography.TextInput: TextStyle
    @Composable get() = TextStyle(
        fontFamily = Pretendard,
        color = White,
        fontSize = 16.sp,
    )
