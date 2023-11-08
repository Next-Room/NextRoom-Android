package com.nextroom.nextroom.presentation.ui.billing

sealed interface BillingEvent {
    data object PurchaseAcknowledged : BillingEvent
    data class PurchaseFailed(val purchaseState: Int) : BillingEvent
}
