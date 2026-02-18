package com.nextroom.nextroom.presentation.ui.tutorial.hint

import com.nextroom.nextroom.presentation.ui.tutorial.TutorialData
import com.nextroom.nextroom.presentation.ui.tutorial.TutorialHint

data class TutorialHintState(
    val hint: TutorialHint = TutorialData.hints.first(),
    val isHintOpened: Boolean = false,
    val isAnswerOpened: Boolean = false,
    val totalHintCount: Int = 3,
    val lastSeconds: Int = 0,
    val showTooltip: Boolean = false
)
