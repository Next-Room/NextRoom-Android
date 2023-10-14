package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.LoginInfo

data class LoginResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: LoginDto,
)

data class LoginDto(
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("accessTokenExpiresIn")
    val accessTokenExpiresIn: Long?,
    @SerializedName("grantType")
    val grantType: String?,
    @SerializedName("refreshToken")
    val refreshToken: String?,
    @SerializedName("shopName")
    val shopName: String?,
)

fun LoginResponse.toDomain(): LoginInfo {
    return LoginInfo(
        shopName = data.shopName ?: "",
        accessToken = data.accessToken ?: "",
        refreshToken = data.refreshToken ?: "",
    )
}
