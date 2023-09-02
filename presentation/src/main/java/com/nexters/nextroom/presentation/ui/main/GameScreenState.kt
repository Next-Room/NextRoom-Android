package com.nexters.nextroom.presentation.ui.main

import com.nexters.nextroom.presentation.model.InputState
import com.nexters.nextroom.presentation.ui.hint.HintState

data class GameScreenState(
    val totalSeconds: Int = 0,
    val lastSeconds: Int = 0,
    val currentInput: String = "",
    val inputState: InputState = InputState.Empty,
    val usedHints: Set<Int> = emptySet(),
    val answerOpenedHints: Set<Int> = emptySet(),
    val totalHintCount: Int = -1,
    val currentHint: HintState? = null,
) {
    val usedHintsCount: Int
        get() = usedHints.size

    val answerOpened: Boolean
        get() = answerOpenedHints.contains(currentHint?.hintId)
}
