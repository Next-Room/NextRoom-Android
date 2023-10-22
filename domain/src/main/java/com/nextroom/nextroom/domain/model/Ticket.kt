package com.nextroom.nextroom.domain.model

data class Ticket(
    val id: Long,
    val plan: String,
    val description: String,
    val originPrice: Int?,
    val sellPrice: Int,
)
