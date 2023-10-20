package com.nextroom.nextroom.presentation.ui

import com.nextroom.nextroom.domain.model.GameState

sealed interface MainEvent {
    data class GoToGameScreen(val gameState: GameState) : MainEvent
    data object GoToAdminCode : MainEvent
}
