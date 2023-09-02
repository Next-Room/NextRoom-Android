package com.nexters.nextroom.presentation.ui

import com.nexters.nextroom.domain.model.GameState

sealed interface MainEvent {
    data class GoToGameScreen(val gameState: GameState) : MainEvent
    object GoToAdminCode : MainEvent
}
