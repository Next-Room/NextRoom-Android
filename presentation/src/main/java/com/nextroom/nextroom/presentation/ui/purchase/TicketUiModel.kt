package com.nextroom.nextroom.presentation.ui.purchase

import com.nextroom.nextroom.domain.model.Ticket

data class TicketUiModel(
    val id: Long,
    val plan: String,
    val description: String,
    val originPrice: Int?,
    val sellPrice: Int,
    val subscribing: Boolean,
) {
    fun toDomain(): Ticket {
        return Ticket(
            id = id,
            plan = plan,
            description = description,
            originPrice = originPrice,
            sellPrice = sellPrice,
        )
    }
}

fun Ticket.toPresentation(subscribing: Boolean): TicketUiModel {
    return TicketUiModel(
        id = id,
        plan = plan,
        description = description,
        originPrice = originPrice,
        sellPrice = sellPrice,
        subscribing = subscribing,
    )
}
