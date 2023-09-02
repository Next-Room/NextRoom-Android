package com.nexters.nextroom.presentation.model

import com.nexters.nextroom.domain.model.Hint
import com.nexters.nextroom.domain.model.ThemeInfo

data class ThemeInfoPresentation(
    val id: Int = 0,
    val title: String = "",
    val timeLimit: Int = 3600,
    val hintLimit: Int = -1,
    val hints: List<Hint> = emptyList(),
    val recentUpdated: Long = 0L,
)

fun ThemeInfo.toPresentation(recentUpdated: Long): ThemeInfoPresentation {
    return ThemeInfoPresentation(
        id = id,
        title = title,
        timeLimit = timeLimit,
        hintLimit = hintLimit,
        hints = hints,
        recentUpdated = recentUpdated,
    )
}
