package com.nextroom.nextroom.domain.model

data class GoogleLoginResponse(
    val shopName: String?,
    val adminCode: String,
    val grantType: String,
    val accessToken: String,
    val accessTokenExpiresIn: Long,
    val refreshToken: String,
    val isComplete: Boolean,
)