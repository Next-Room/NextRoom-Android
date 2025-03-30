package com.nextroom.nextroom.domain.model

data class AdditionalUserInfoResponse(
    val isComplete: Boolean,
    val shopName: String,
    val adminCode: String?,
)