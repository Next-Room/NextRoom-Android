package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName

data class AdditionalUserInfoRequestDto(
    @SerializedName("name") val shopName: String,
    @SerializedName("signupSource") val signupSource: String,
    @SerializedName("comment") val signupReason: String,
    @SerializedName("type") val type: Int = 3, // 앱에서 가입하는 경우 3 고정
    @SerializedName("adsConsent") val marketingTermAgreed: Boolean,
)
