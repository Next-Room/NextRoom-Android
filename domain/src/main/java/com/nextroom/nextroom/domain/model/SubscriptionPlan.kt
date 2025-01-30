package com.nextroom.nextroom.domain.model

import java.math.BigDecimal

data class SubscriptionPlan(
    val url: String,
    val plans: List<Plan>

) {
    data class Plan(
        val id: Int,
        val subscriptionProductId: String,
        val planId: String,
        val productName: String,
        val description: String,
        val subDescription: String,
        val originPrice: BigDecimal,
        val sellPrice: BigDecimal
    )
}