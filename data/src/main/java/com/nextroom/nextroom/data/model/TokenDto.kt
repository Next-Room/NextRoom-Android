package com.nextroom.nextroom.data.model

data class TokenDto(
    val grandType: String,
    val accessToken: String,
    val accessTokenExpiresIn: Long,
    val refreshToken: String,
)
