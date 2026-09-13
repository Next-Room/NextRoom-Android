package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.LoginInfo

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
) {
    fun toDomain(): LoginInfo {
        return LoginInfo(
            shopName = shopName ?: "",
            accessToken = accessToken ?: "",
            refreshToken = refreshToken ?: "",
        )
    }
}
