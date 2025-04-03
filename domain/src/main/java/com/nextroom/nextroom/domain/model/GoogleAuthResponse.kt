package com.nextroom.nextroom.domain.model

data class GoogleAuthResponse(
    val idToken: String,
    val email: String,
    val name: String?,
)