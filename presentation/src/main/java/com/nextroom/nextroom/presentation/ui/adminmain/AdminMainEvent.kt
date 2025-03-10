package com.nextroom.nextroom.presentation.ui.adminmain

import com.nextroom.nextroom.domain.model.SubscribeStatus

sealed interface AdminMainEvent {
    data object NetworkError : AdminMainEvent
    data object UnknownError : AdminMainEvent
    data class ClientError(val message: String) : AdminMainEvent
    data object InAppReview : AdminMainEvent
    data object RecommendBackgroundCustom: AdminMainEvent
    data class ReadyToGameStart(val subscribeStatus: SubscribeStatus) : AdminMainEvent
    data object NeedToSetPassword : AdminMainEvent
    data class NeedToCheckPasswordForStartGame(val themeId: String) : AdminMainEvent
}
