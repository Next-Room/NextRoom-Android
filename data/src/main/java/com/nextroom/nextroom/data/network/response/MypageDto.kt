package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.SubscribeItem
import com.nextroom.nextroom.domain.model.UserSubscription

data class MypageDto(
    @SerializedName("id") val id: Long,
    @SerializedName("subStatus") val subsName: String, // LARGE
    @SerializedName("createdAt") val createdAt: String, // 2023-10-24 02:29:57
    @SerializedName("expiryDate") val expiryDate: String, // 2023-11-23
) {
    fun toDomain(): UserSubscription {
        return UserSubscription(
            type = SubscribeItem(id = id, name = subsName),
            createdAt = createdAt,
            expiryDate = expiryDate,
        )
    }
}
