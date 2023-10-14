package com.nextroom.nextroom.domain.model

data class GameState(
    val playingThemeId: Int,
    val playing: Boolean,
    val timeLimit: Int,
    val lastSeconds: Int, // 보정된 시간
    val hintLimit: Int,
    val usedHints: Set<Int>,
)
