package com.nextroom.nextroom.presentation.ui.purchase

import com.nextroom.nextroom.domain.model.Ticket

data class TicketUiModel(
    val id: String,
    val subscriptionProductId: String,
    val planId: String,
    val productName: String,
    val description: String,
    val subDescription: String,
    val originPrice: Int?,
    val sellPrice: Int,
    val discountRate: Int,
    val subscribing: Boolean,
) {
    fun toDomain(): Ticket {
        return Ticket(
            id = id,
            subscriptionProductId = subscriptionProductId,
            planId = planId,
            productName = productName,
            description = description,
            subDescription = subDescription,
            originPrice = originPrice,
            sellPrice = sellPrice,
            discountRate = discountRate,
        )
    }
}

fun Ticket.toPresentation(subscribing: Boolean): TicketUiModel {
    return TicketUiModel(
        id = id,
        subscriptionProductId = subscriptionProductId,
        planId = planId,
        productName = productName,
        description = description,
        subDescription = subDescription,
        originPrice = originPrice,
        sellPrice = sellPrice,
        discountRate = discountRate,
        subscribing = subscribing,
    )
}
