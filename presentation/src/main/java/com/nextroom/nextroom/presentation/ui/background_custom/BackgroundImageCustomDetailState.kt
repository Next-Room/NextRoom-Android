package com.nextroom.nextroom.presentation.ui.background_custom

import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

data class BackgroundImageCustomDetailState(
    val themeInfoPresentation: ThemeInfoPresentation
)

sealed interface BackgroundImageCustomDetailEvent {
    data object SaveSuccess : BackgroundImageCustomDetailEvent
    data object UnknownError : BackgroundImageCustomDetailEvent
}
