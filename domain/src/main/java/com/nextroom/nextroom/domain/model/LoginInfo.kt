package com.nextroom.nextroom.domain.model

data class LoginInfo(
    val adminCode: String,
    val shopName: String,
    val accessToken: String,
    val refreshToken: String,
)
