package com.nextroom.nextroom.presentation.ui.background_custom

import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

data class BackgroundCustomState(
    val themes: List<ThemeInfoPresentation>,
    val userSubscribeStatus: SubscribeStatus
)

sealed interface BackgroundCustomEvent {
    data class ToggleImageError(val errorRes: Int) : BackgroundCustomEvent
    data object NetworkError : BackgroundCustomEvent
    data object UnknownError : BackgroundCustomEvent
    data class ClientError(val message: String) : BackgroundCustomEvent
    data object ToggleNotAllowed : BackgroundCustomEvent
    data class ThemeImageClicked(val theme: ThemeInfoPresentation): BackgroundCustomEvent
}
