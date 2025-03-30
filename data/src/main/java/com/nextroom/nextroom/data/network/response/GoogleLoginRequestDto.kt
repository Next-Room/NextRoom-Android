package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName

data class GoogleLoginRequestDto(
    @SerializedName("idToken") val idToken: String,
)