package com.nexters.nextroom.data.network.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("adminCode") val adminCode: String,
    @SerializedName("password") val password: String,
)
