package com.nextroom.nextroom.domain.model

data class UserSubscribe(
    val type: SubscribeItem = SubscribeItem(),
    val period: String = "",
)

data class SubscribeItem(
    val id: Long = 0,
    val name: String = "",
)

data class UserSubscribeStatus(
    val subscriptionId: Long = 0L,
    val subscribeStatus: SubscribeStatus = SubscribeStatus.None,
    val expiryDate: String = "",
    val subscribedAt: String = "",
    val createdAt: String = "",
    val modifiedAt: String = "",
)

@Suppress("NonAsciiCharacters")
enum class SubscribeStatus {
    None, 무료체험중, 무료체험끝, 유예기간만료, 구독중, 구독만료 // ktlint-disable enum-entry-name-case
}
