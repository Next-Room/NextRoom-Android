package com.nextroom.nextroom.presentation.ui.login

import com.nextroom.nextroom.presentation.model.UiText

sealed interface LoginEvent {
    data class LoginFailed(val message: String) : LoginEvent
    data class ShowMessage(val message: UiText) : LoginEvent
    data object GoToOnboardingScreen : LoginEvent
    data object GoToMainScreen : LoginEvent
}
