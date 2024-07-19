package com.nextroom.nextroom.presentation.ui.purchase

import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.Ticket
import com.nextroom.nextroom.domain.model.UserSubscription

data class PurchaseState(
    val subscribeStatus: SubscribeStatus = SubscribeStatus.Default,
    val userSubscription: UserSubscription? = UserSubscription(),
    private val tickets: List<Ticket> = emptyList(),
) {
    val ticketsForUi: List<TicketUiModel>
        get() = tickets.map {
            it.toPresentation(it.id == userSubscription?.type?.id)
        }
}
