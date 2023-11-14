package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.Ticket

data class TicketDto(
    @SerializedName("id") val id: String,
    @SerializedName("plan") val plan: String,
    @SerializedName("description") val description: String,
    @SerializedName("originPrice") val originPrice: Int?,
    @SerializedName("sellPrice") val sellPrice: Int,
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
