package com.nextroom.nextroom.presentation.ui.billing

sealed interface BillingEvent {
    data object PurchaseAcknowledged : BillingEvent
    data class PurchaseFailed(val errorMessage: String = "", val purchaseState: Int? = null) : BillingEvent
}
