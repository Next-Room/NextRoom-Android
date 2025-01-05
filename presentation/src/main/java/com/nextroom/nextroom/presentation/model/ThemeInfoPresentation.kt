package com.nextroom.nextroom.presentation.model

import android.os.Parcelable
import com.nextroom.nextroom.domain.model.Hint
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
    val themeImageUrl: String? = null
) : Parcelable

fun ThemeInfo.toPresentation(recentUpdated: Long): ThemeInfoPresentation {
    return ThemeInfoPresentation(
        id = id,
        title = title,
        timeLimit = timeLimitInMinute,
        hintLimit = hintLimit,
        hints = hints,
        recentUpdated = recentUpdated,
        useTimerUrl = useTimerUrl,
        themeImageUrl = themeImageUrl
    )
}
