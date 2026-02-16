package com.nextroom.nextroom.presentation.ui.tutorial.timer

import com.nextroom.nextroom.presentation.model.InputState
import com.nextroom.nextroom.presentation.ui.tutorial.TutorialData

data class TutorialTimerState(
    val totalSeconds: Int = TutorialData.DEFAULT_TIME_LIMIT_SECONDS,
    val lastSeconds: Int = TutorialData.DEFAULT_TIME_LIMIT_SECONDS,
    val currentInput: String = "",
    val inputState: InputState = InputState.Empty,
    val openedHintCount: Int = 0,
    val totalHintCount: Int = 3
)
