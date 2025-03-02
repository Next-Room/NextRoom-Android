package com.nextroom.nextroom.presentation.ui.theme_select

import com.nextroom.nextroom.domain.model.SubscribeStatus

sealed interface ThemeSelectEvent {
    data object NetworkError : ThemeSelectEvent
    data object UnknownError : ThemeSelectEvent
    data class ClientError(val message: String) : ThemeSelectEvent
    data object InAppReview : ThemeSelectEvent
    data object RecommendBackgroundCustom : ThemeSelectEvent
    data class ReadyToGameStart(val subscribeStatus: SubscribeStatus) : ThemeSelectEvent
    data object NeedToSetPassword : ThemeSelectEvent
    data class NeedToCheckPasswordForStartGame(val themeId: String) : ThemeSelectEvent
}
