package com.nextroom.nextroom.presentation.ui.tutorial.timer

import com.nextroom.nextroom.presentation.ui.tutorial.TutorialHint

sealed interface TutorialTimerEvent {
    data class OpenHint(val hint: TutorialHint) : TutorialTimerEvent
    object TimerFinished : TutorialTimerEvent
    object ExitTutorial : TutorialTimerEvent
}
