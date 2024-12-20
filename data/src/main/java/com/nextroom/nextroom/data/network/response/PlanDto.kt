package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.Plan
import com.nextroom.nextroom.domain.model.Ticket

data class TicketDto(
    @SerializedName("url")
    val url: String,
    @SerializedName("plans")
    val plans: List<PlanDto>
) {
    fun toDomainModel(): Ticket {
        return Ticket(
            url = url,
            plans = plans.map { it.toDomain() }
        )
    }
}

data class PlanDto(
    @SerializedName("id") val id: String,
    @SerializedName("subscriptionProductId") val subscriptionProductId: String,
    @SerializedName("planId") val planId: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("description") val description: String,
    @SerializedName("subDescription") val subDescription: String,
    @SerializedName("originPrice") val originPrice: Int?,
    @SerializedName("sellPrice") val sellPrice: Int,
    @SerializedName("discountRate") val discountRate: Int,
) {
    fun toDomain(): Plan {
        return Plan(
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
