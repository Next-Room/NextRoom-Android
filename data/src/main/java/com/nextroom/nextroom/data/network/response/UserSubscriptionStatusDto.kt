package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.UserSubscribeStatus

data class UserSubscriptionStatusDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("userStatus")
    val status: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("expiryDate")
    val expiryDate: String,
) {
    fun toDomain(): UserSubscribeStatus {
        val subsStatus = when (status.trim().uppercase()) {
//            "FREE" -> SubscribeStatus.Free
//            "HOLD" -> SubscribeStatus.Hold
//            "EXPIRATION" -> SubscribeStatus.Expiration
//            "SUBSCRIPTION" -> SubscribeStatus.Subscription
//            "SUBSCRIPTION_EXPIRATION" -> SubscribeStatus.SubscriptionExpiration
//            else -> SubscribeStatus.None
            else -> SubscribeStatus.Default
        }
        return UserSubscribeStatus(
            subscriptionId = id,
            subscribeStatus = subsStatus,
            expiryDate = expiryDate,
            createdAt = createdAt,
        )
    }
}
