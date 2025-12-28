package com.nextroom.nextroom.presentation.ui.hint

sealed interface HintEvent {
    data object NetworkError : HintEvent
    data object UnknownError : HintEvent
    data class ClientError(val message: String) : HintEvent
}
