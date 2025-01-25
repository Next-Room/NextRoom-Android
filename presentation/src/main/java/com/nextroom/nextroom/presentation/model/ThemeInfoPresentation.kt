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
        val opacity: Int,
        val scaleFactor: Float,
        val focusX: Float,
        val focusY: Float
    ) : Parcelable
}

fun ThemeInfoPresentation.toDomain(themeImageCustomInfo: ThemeImageCustomInfo?): ThemeInfo {
    return ThemeInfo(
        id = id,
        title = title,
        timeLimitInMinute = timeLimit,
        hintLimit = hintLimit,
        hints = hints,
        useTimerUrl = useTimerUrl,
        themeImageUrl = themeImageUrl,
        themeImageCustomInfo = themeImageCustomInfo
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
        scaleFactor = scaleFactor,
        focusX = focusX,
        focusY = focusY,
        opacity = opacity
    )
}