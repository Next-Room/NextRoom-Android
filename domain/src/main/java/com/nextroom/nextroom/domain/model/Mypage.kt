package com.nextroom.nextroom.domain.model

data class Mypage(
    val id: String,
    val name: String,
    val status: SubscribeStatus,
    val startDate: String?,
    val expiryDate: String?,
    val createdAt: String,
)