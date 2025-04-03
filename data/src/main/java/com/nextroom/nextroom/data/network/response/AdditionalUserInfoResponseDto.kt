package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.AdditionalUserInfoResponse

data class AdditionalUserInfoResponseDto(
    @SerializedName("isComplete") val isComplete: Boolean,
    @SerializedName("shopName") val shopName: String,
    @SerializedName("adminCode") val adminCode: String?,
) {
    fun toDomainModel(): AdditionalUserInfoResponse {
        return AdditionalUserInfoResponse(
            isComplete = isComplete,
            shopName = shopName,
            adminCode = adminCode,
        )
    }
}