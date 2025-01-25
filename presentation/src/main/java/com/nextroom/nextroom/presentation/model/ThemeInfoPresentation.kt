package com.nextroom.nextroom.presentation.model

import android.os.Parcelable
import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.domain.model.ThemeImageCustomInfo
import com.nextroom.nextroom.domain.model.ThemeInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThemeInfoPresentation(
    val id: Int = 0,
    val title: String = "",
    val timeLimit: Int = 0,
    val hintLimit: Int = -1,
    val hints: List<Hint> = emptyList(),
    val recentUpdated: Long = 0L,
    val useTimerUrl: Boolean = false,
    val themeImageUrl: String? = null,
    val themeImageCustomInfo: ThemeImageCustomInfoUi? = null,
) : Parcelable {

    @Parcelize
    data class ThemeImageCustomInfoUi(
        val opacity: Int? = null,
        val left: Float? = null,
        val top: Float? = null,
        val right: Float? = null,
        val bottom: Float? = null,
    ) : Parcelable {

        fun toDomain(): ThemeImageCustomInfo {
            return ThemeImageCustomInfo(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                opacity = opacity
            )
        }
    }
}

fun ThemeInfoPresentation.toDomain(themeImageCustomInfo: ThemeInfoPresentation.ThemeImageCustomInfoUi?): ThemeInfo {
    return ThemeInfo(
        id = id,
        title = title,
        timeLimitInMinute = timeLimit,
        hintLimit = hintLimit,
        hints = hints,
        useTimerUrl = useTimerUrl,
        themeImageUrl = themeImageUrl,
        themeImageCustomInfo = themeImageCustomInfo?.toDomain()
    )
}

fun ThemeInfo.toPresentation(recentUpdated: Long): ThemeInfoPresentation {
    return ThemeInfoPresentation(
        id = id,
        title = title,
        timeLimit = timeLimitInMinute,
        hintLimit = hintLimit,
        hints = hints,
        recentUpdated = recentUpdated,
        useTimerUrl = useTimerUrl,
        themeImageUrl = themeImageUrl,
        themeImageCustomInfo = themeImageCustomInfo?.toPresentation()
    )
}

fun ThemeImageCustomInfo.toPresentation(): ThemeInfoPresentation.ThemeImageCustomInfoUi {
    return ThemeInfoPresentation.ThemeImageCustomInfoUi(
        left = left,
        top = top,
        right = right,
        bottom = bottom,
        opacity = opacity
    )
}