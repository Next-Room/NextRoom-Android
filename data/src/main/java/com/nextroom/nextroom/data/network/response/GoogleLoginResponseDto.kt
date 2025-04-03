package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.GoogleLoginResponse

data class GoogleLoginResponseDto(
    @SerializedName("shopName") val shopName: String?,
    @SerializedName("adminCode") val adminCode: String,
    @SerializedName("grantType") val grantType: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("accessTokenExpiresIn") val accessTokenExpiresIn: Long,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("isComplete") val isComplete: Boolean,
) {
    fun toDomainModel(): GoogleLoginResponse {
        return GoogleLoginResponse(
            shopName = shopName,
            adminCode = adminCode,
            grantType = grantType,
            accessToken = accessToken,
            accessTokenExpiresIn = accessTokenExpiresIn,
            refreshToken = refreshToken,
            isComplete = isComplete
        )
    }
}