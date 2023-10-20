package com.nextroom.nextroom.domain.model

data class UserSubscribe(
    val id: Long,
    val type: SubscribeItem,
    val period: String,
)

data class SubscribeItem(
    val id: Long,
    val name: String,
)

data class UserSubscribeStatus(
    val subscribeStatus: SubscribeStatus = SubscribeStatus.무료체험중,
    val expiredDate: String,
)

enum class SubscribeStatus {
    무료체험중, 무료체험끝, 유예기간만료, 구독중, 구독만료
}
