package com.nextroom.nextroom.presentation.ui.login

import com.nextroom.nextroom.presentation.model.UiText

sealed interface EmailLoginEvent {
    data class EmailLoginFailed(val message: String) : EmailLoginEvent
    data class ShowMessage(val message: UiText) : EmailLoginEvent
    data object GoToGameScreen : EmailLoginEvent
    data object GoogleAuthFailed : EmailLoginEvent
    data object GoogleLoginFailed : EmailLoginEvent
    data class NeedAdditionalUserInfo(val shopName: String?) : EmailLoginEvent
}
