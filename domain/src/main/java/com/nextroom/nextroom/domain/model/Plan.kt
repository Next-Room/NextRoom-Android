package com.nextroom.nextroom.domain.model

data class Plan(
    val id: String,
    val subscriptionProductId: String,
    val planId: String,
    val productName: String,
    val description: String,
    val subDescription: String,
    val originPrice: Int?,
    val sellPrice: Int,
    val discountRate: Int,
)
