package com.nextroom.nextroom.presentation.ui.main

import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.model.Hint

sealed interface GameEvent {
    data class OnOpenHint(
        val hint: Hint,
        val subscribeStatus: SubscribeStatus
    ) : GameEvent

    data object GameFinish : GameEvent
    data object ClearHintCode : GameEvent

    data object ShowAvailableHintExceedError : GameEvent
    data object NewGame : GameEvent
}
