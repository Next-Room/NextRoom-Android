package com.nextroom.nextroom.presentation.ui

sealed interface SubscriptionPaymentEvent {
    data object NetworkError : SubscriptionPaymentEvent
    data object UnknownError : SubscriptionPaymentEvent
    data class ClientError(val message: String) : SubscriptionPaymentEvent
}
