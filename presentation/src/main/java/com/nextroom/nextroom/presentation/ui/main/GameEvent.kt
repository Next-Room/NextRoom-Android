package com.nextroom.nextroom.presentation.ui.main

import com.nextroom.nextroom.presentation.model.Hint

sealed interface GameEvent {
    data class OnOpenHint(
        val hint: Hint,
    ) : GameEvent

    data object GameFinish : GameEvent
    data object ClearHintCode : GameEvent

    data object ShowAvailableHintExceedError : GameEvent
    data object NewGame : GameEvent
}
