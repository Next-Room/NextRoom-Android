package com.nextroom.nextroom.presentation.ui.main

import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.model.Hint

sealed interface TimerEvent {
    data class OnOpenHint(
        val hint: Hint,
        val subscribeStatus: SubscribeStatus
    ) : TimerEvent

    data object TimerFinish : TimerEvent
    data object ClearHintCode : TimerEvent

    data object ShowAvailableHintExceedError : TimerEvent
    data object NewTimer : TimerEvent
}
