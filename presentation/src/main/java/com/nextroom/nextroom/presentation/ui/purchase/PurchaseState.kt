package com.nextroom.nextroom.presentation.ui.purchase

import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.Ticket
import com.nextroom.nextroom.domain.model.UserSubscribe

data class PurchaseState(
    val subscribeStatus: SubscribeStatus = SubscribeStatus.None,
    val userSubscribe: UserSubscribe? = UserSubscribe(),
    private val tickets: List<Ticket> = emptyList(),
) {
    val ticketsForUi: List<TicketUiModel>
        get() = tickets.map {
            it.toPresentation(it.id == userSubscribe?.type?.id)
        }
}
