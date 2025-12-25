package com.nextroom.nextroom.presentation.ui.main

import com.nextroom.nextroom.presentation.model.Hint

sealed interface TimerEvent {
    data class OnOpenHint(
        val hint: Hint
    ) : TimerEvent

    data object TimerFinish : TimerEvent
    data object ClearHintCode : TimerEvent

    data object ShowAvailableHintExceedError : TimerEvent
    data object NewTimer : TimerEvent
}
