package com.nextroom.nextroom.domain.model

data class ThemeInfo(
    val id: Int = 0,
    val title: String = "",
    val timeLimitInMinute: Int = 60,
    val hintLimit: Int = -1,
    val hints: List<Hint> = emptyList(),
    val useTimerUrl: Boolean = false,
    val themeImageUrl: String? = null,
    val themeImageCustomInfo: ThemeImageCustomInfo? = null,
)

data class ThemeImageCustomInfo(
    val left: Float? = null,
    val top: Float? = null,
    val right: Float? = null,
    val bottom: Float? = null,
    val opacity: Int? = null
)
