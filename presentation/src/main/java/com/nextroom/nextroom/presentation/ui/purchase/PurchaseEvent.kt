package com.nextroom.nextroom.presentation.ui.purchase

import com.nextroom.nextroom.domain.model.Ticket

sealed interface PurchaseEvent {
    data class StartPurchase(val ticket: Ticket) : PurchaseEvent
}
