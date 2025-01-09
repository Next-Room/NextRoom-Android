package com.nextroom.nextroom.presentation.ui

import com.nextroom.nextroom.domain.model.GameState
import com.nextroom.nextroom.domain.model.SubscribeStatus

sealed interface MainEvent {
    data class GoToGameScreen(val gameState: GameState, val subscribeStatus: SubscribeStatus) : MainEvent
    data object GoToLoginScreen : MainEvent
    data object ShowForceUpdateDialog : MainEvent
}
