package com.nextroom.nextroom.domain.model

data class ThemeInfo(
    val id: Int = 0,
    val title: String = "",
    val timeLimit: Int = 3600,
    val hintLimit: Int = -1,
    val hints: List<Hint> = emptyList(),
)
