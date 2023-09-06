package com.nexters.nextroom.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
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
        fontSize = TextUnit(54f, TextUnitType.Sp),
        fontWeight = FontWeight.SemiBold,
    ),
    titleLarge = TextStyle(
        fontFamily = Pretendard,
        color = White,
        fontSize = TextUnit(32f, TextUnitType.Sp),
        fontWeight = FontWeight.Bold,
    ),
    titleMedium = TextStyle(
        fontFamily = Pretendard,
        color = White,
        fontSize = TextUnit(24f, TextUnitType.Sp),
        fontWeight = FontWeight.SemiBold,
    ),
    bodyMedium = TextStyle(
        fontFamily = Pretendard,
        color = White,
        fontSize = TextUnit(20f, TextUnitType.Sp),
    ),
    bodySmall = TextStyle(
        fontFamily = Poppins,
        color = White,
        fontSize = TextUnit(14f, TextUnitType.Sp),
        fontWeight = FontWeight.Medium,
    ),
    labelMedium = TextStyle(
        fontFamily = Poppins,
        color = Gray01,
        fontSize = TextUnit(16f, TextUnitType.Sp),
    ),
    labelSmall = TextStyle(
        fontFamily = Pretendard,
        color = Gray01,
        fontSize = TextUnit(12f, TextUnitType.Sp),
    ),
)

val Typography.EngButton: TextStyle
    @Composable get() = labelMedium.copy(
        fontFamily = Poppins,
        color = Dark01,
        fontSize = TextUnit(16f, TextUnitType.Sp),
    )

val Typography.KorButton: TextStyle
    @Composable get() = labelMedium.copy(
        fontFamily = Pretendard,
        color = Dark01,
        fontSize = TextUnit(16f, TextUnitType.Sp),
    )

val Typography.EngSubButton: TextStyle
    @Composable get() = labelMedium.copy(
        fontFamily = Poppins,
        color = Dark01,
        fontSize = TextUnit(14f, TextUnitType.Sp),
    )

val Typography.KorSubButton: TextStyle
    @Composable get() = labelMedium.copy(
        fontFamily = Pretendard,
        color = Dark01,
        fontSize = TextUnit(14f, TextUnitType.Sp),
    )

val Typography.KeypadButton: TextStyle
    @Composable get() = labelMedium.copy(
        fontFamily = Poppins,
        color = White,
        fontSize = TextUnit(24f, TextUnitType.Sp),
    )
