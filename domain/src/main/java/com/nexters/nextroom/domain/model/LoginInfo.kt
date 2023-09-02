package com.nexters.nextroom.domain.model

data class LoginInfo(
    val shopName: String,
    val accessToken: String,
    val refreshToken: String,
)
