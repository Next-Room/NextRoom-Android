package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.UserSubscribeStatus

data class UserSubscriptionStatusDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("userStatus")
    val status: String,
    @SerializedName("expiryDate")
    val expiryDate: String,
    @SerializedName("subscribedAt")
    val subscribedAt: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("modifiedAt")
    val modifiedAt: String,
) {
    fun toDomain(): UserSubscribeStatus {
        val subsStatus = when (status.trim().uppercase()) {
            "SUBSCRIPTION" -> SubscribeStatus.구독중
            else -> SubscribeStatus.무료체험중 // TODO 서버 enum 과 일치시키기
        }
        return UserSubscribeStatus(
            subscriptionId = id,
            subscribeStatus = subsStatus,
            expiryDate = expiryDate,
            subscribedAt = subscribedAt,
            createdAt = createdAt,
            modifiedAt = modifiedAt,
        )
    }
}
