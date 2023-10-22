package com.nextroom.nextroom.presentation.ui.purchase

sealed interface PurchaseEvent {
    fun onPurchase(id: Long): PurchaseEvent
}
