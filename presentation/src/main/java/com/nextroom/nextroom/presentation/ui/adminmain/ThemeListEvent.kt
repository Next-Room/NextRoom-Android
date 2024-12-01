package com.nextroom.nextroom.presentation.ui.adminmain

sealed interface ThemeListEvent {
    data object NetworkError : ThemeListEvent
    data object UnknownError : ThemeListEvent
    data class ClientError(val message: String) : ThemeListEvent
    data object InAppReview : ThemeListEvent
}
