package com.nextroom.nextroom.presentation.ui.login

import com.nextroom.nextroom.presentation.model.UiText

sealed interface EmailLoginEvent {
    data class EmailLoginFailed(val message: String) : EmailLoginEvent
    data class ShowMessage(val message: UiText) : EmailLoginEvent
    data object GoToOnboardingScreen : EmailLoginEvent
    data object GoToGameScreen : EmailLoginEvent
}
