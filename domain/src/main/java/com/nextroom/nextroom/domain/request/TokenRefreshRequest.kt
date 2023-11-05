package com.nextroom.nextroom.domain.request

data class TokenRefreshRequest(
    val accessToken: String,
    val refreshToken: String,
)
