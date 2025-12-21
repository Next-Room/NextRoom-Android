package com.nextroom.nextroom.presentation.ui.game

import com.nextroom.nextroom.domain.model.Hint


sealed interface GameSharedEvent {
    data class ShowToast(val message: String) : GameSharedEvent
    // Other shared events if needed, e.g. for navigation
}
