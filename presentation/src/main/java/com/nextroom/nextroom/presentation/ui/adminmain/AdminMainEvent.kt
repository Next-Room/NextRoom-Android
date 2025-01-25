package com.nextroom.nextroom.presentation.ui.adminmain

sealed interface AdminMainEvent {
    data object NetworkError : AdminMainEvent
    data object UnknownError : AdminMainEvent
    data class ClientError(val message: String) : AdminMainEvent
    data object InAppReview : AdminMainEvent
    data object RecommendBackgroundCustom: AdminMainEvent
}
