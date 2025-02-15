package com.nextroom.nextroom.domain.model

data class GameState(
    val playingThemeId: Int,
    val timeLimitInMinute: Int, // ex) 60 = 60분
    val startTime: Long,
    val lastSeconds: Int, // 보정된 시간
    val hintLimit: Int,
    val usedHints: Set<Int>,
    val useTimerUrl: Boolean,
    val themeImageUrl: String? = null,
    val themeImageCustomInfo: ThemeImageCustomInfo? = null,
)
