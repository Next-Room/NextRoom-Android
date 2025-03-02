package com.nextroom.nextroom.presentation.ui.main

import com.nextroom.nextroom.domain.model.ThemeImageCustomInfo
import com.nextroom.nextroom.presentation.model.InputState

data class TimerScreenState(
    val totalSeconds: Int = 0,
    val lastSeconds: Int = 0,
    val currentInput: String = "",
    val inputState: InputState = InputState.Empty,
    val usedHints: Set<Int> = emptySet(),
    val answerOpenedHints: Set<Int> = emptySet(),
    val totalHintCount: Int = -1,
    val startTime: Long = -1,
    val themeImageUrl: String? = null,
    val themeImageCustomInfo: ThemeImageCustomInfo? = null,
    val themeImageEnabled: Boolean = false
) {
    val usedHintsCount: Int
        get() = usedHints.size
}
