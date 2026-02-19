package com.nextroom.nextroom.presentation.common.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nextroom.nextroom.presentation.R

object NextRoomFontFamily {
    val Pretendard = FontFamily(
        Font(R.font.pretendard_regular, FontWeight.Normal),
        Font(R.font.pretendard_medium, FontWeight.Medium),
        Font(R.font.pretendard_semi_bold, FontWeight.SemiBold),
        Font(R.font.pretendard_bold, FontWeight.Bold)
    )

    val Poppins = FontFamily(
        Font(R.font.poppins_medium, FontWeight.Medium),
        Font(R.font.poppins_semi_bold, FontWeight.SemiBold)
    )

    val NotoSansMono = FontFamily(
        Font(R.font.notosansmono_semibold, FontWeight.SemiBold)
    )
}

object NRTypo {
    object Title {
        val size24SemiBold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = NRColor.White
            )

        val size24Medium: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
                color = NRColor.White
            )

        val size20SemiBold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = NRColor.White
            )

        val size20Medium: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = NRColor.White
            )

        val size18SemiBold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = NRColor.White
            )

        val size16SemiBold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = NRColor.White
            )

        val size16Medium: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = NRColor.White
            )
    }

    // ===== Body Styles =====
    object Body {
        val size16Medium: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = NRColor.White
            )

        val size16Regular: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = NRColor.White
            )

        val size14Medium: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = NRColor.White
            )

        val size14Regular: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = NRColor.White
            )

        val size12Medium: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = NRColor.White
            )

        val size12Regular: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = NRColor.White
            )
    }

    // ===== Caption Styles =====
    object Caption {
        val size12SemiBold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = NRColor.White
            )

        val size12Medium: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = NRColor.White
            )
    }

    // ===== Legacy Pretendard Styles =====
    object Pretendard {
        val size12: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                color = NRColor.White
            )

        val size14: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 17.sp,
                color = NRColor.White
            )

        val size14SemiBold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 17.sp,
                color = NRColor.White
            )

        val size16: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                color = NRColor.White
            )

        val size16Bold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                color = NRColor.White
            )

        val size16SemiBold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                color = NRColor.White
            )

        val size18: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                color = NRColor.White
            )

        val size18SemiBold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                color = NRColor.White
            )

        val size20: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                color = NRColor.White
            )

        val size20SemiBold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                color = NRColor.White
            )

        val size24: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                color = NRColor.White
            )

        val size24Bold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                color = NRColor.White
            )

        val size32: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                color = NRColor.White
            )

        val size32Bold: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                color = NRColor.White
            )
    }

    // ===== NotoSansMono Styles =====
    object NotoSansMono {
        val size54: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.NotoSansMono,
                fontWeight = FontWeight.SemiBold,
                fontSize = 54.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                color = NRColor.White
            )
    }

    // ===== Poppins Styles =====
    object Poppins {
        val size14: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 13.sp,
                color = NRColor.White
            )

        val size16: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 14.sp,
                color = NRColor.White
            )

        val size18: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 16.sp,
                color = NRColor.White
            )

        val size20: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 18.sp,
                color = NRColor.White
            )

        val size24: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 22.sp,
                color = NRColor.White
            )

        val size54: TextStyle
            @Composable get() = TextStyle(
                fontFamily = NextRoomFontFamily.Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 54.sp,
                lineHeight = 49.sp,
                color = NRColor.White
            )
    }
}
