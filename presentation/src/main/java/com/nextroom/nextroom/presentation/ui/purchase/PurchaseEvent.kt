package com.nextroom.nextroom.presentation.ui.purchase

sealed interface PurchaseEvent {
    data class StartPurchase(
        val productId: String,
        val tag: String,
        val upDowngrade: Boolean,
    ) : PurchaseEvent
}
