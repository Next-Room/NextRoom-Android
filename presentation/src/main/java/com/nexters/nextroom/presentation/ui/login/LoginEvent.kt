package com.nexters.nextroom.presentation.ui.login

import com.nexters.nextroom.presentation.model.UiText

sealed interface LoginEvent {
    data class LoginFailed(val message: String) : LoginEvent
    data class ShowMessage(val message: UiText) : LoginEvent
}
