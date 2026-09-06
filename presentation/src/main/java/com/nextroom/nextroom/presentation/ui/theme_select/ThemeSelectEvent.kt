package com.nextroom.nextroom.presentation.ui.theme_select

import com.nextroom.nextroom.domain.model.SubscribeStatus

sealed interface ThemeSelectEvent {
    data object NetworkError : ThemeSelectEvent
    data object UnknownError : ThemeSelectEvent
    data class ClientError(val message: String) : ThemeSelectEvent
    data object InAppReview : ThemeSelectEvent
    data class ReadyToGameStart(val subscribeStatus: SubscribeStatus) : ThemeSelectEvent

    /** 무료 체험 자격이 있는 미구독자 → 무료 체험 안내 화면 */
    data object NeedFreeTrialForGameStart : ThemeSelectEvent

    /** 무료 체험 자격이 없는 미구독자 → 기존 구독 안내(구매) 화면 */
    data object NeedSubscriptionForGameStart : ThemeSelectEvent

    data object NeedToSetPassword : ThemeSelectEvent
    data class NeedToCheckPasswordForStartGame(val themeId: String) : ThemeSelectEvent
    data object NeedToCheckPasswordForManageThemes : ThemeSelectEvent
    data object GuidePopupNotSeen : ThemeSelectEvent
}
