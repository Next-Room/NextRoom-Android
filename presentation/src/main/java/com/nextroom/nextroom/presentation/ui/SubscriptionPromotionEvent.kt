package com.nextroom.nextroom.presentation.ui

sealed interface SubscriptionPromotionEvent {
    data object NetworkError : SubscriptionPromotionEvent
    data object UnknownError : SubscriptionPromotionEvent
    data class ClientError(val message: String) : SubscriptionPromotionEvent
}
