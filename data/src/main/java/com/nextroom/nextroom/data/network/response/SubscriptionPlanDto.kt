package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.SubscriptionPlan
import java.math.BigDecimal

data class SubscriptionPlanDto(
    @SerializedName("url")
    val url: String,
    @SerializedName("plans")
    val plans: List<PlanDto>
) {

    data class PlanDto(
        @SerializedName("id")
        val id: Int,
        @SerializedName("subscriptionProductId")
        val subscriptionProductId: String,
        @SerializedName("planId")
        val planId: String,
        @SerializedName("productName")
        val productName: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("subDescription")
        val subDescription: String,
        @SerializedName("originPrice")
        val originPrice: BigDecimal,
        @SerializedName("sellPrice")
        val sellPrice: BigDecimal
    ) {
        fun toDomainModel(): SubscriptionPlan.Plan {
            return SubscriptionPlan.Plan(
                id = id,
                subscriptionProductId = subscriptionProductId,
                planId = planId,
                productName = productName,
                description = description,
                subDescription = subDescription,
                originPrice = originPrice,
                sellPrice = sellPrice
            )
        }
    }

    fun toDomainModel(): SubscriptionPlan {
        return SubscriptionPlan(
            url = url,
            plans = plans.map { it.toDomainModel() }
        )
    }
}