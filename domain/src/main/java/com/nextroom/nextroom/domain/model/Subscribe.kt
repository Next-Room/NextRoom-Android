package com.nextroom.nextroom.domain.model

/**
 * 마이페이지에 표시할 유저 구독 정보
 *
 * @property type
 * @property createdAt
 * @property expiryDate
 */
data class UserSubscription(
    val type: SubscribeItem = SubscribeItem(),
    val createdAt: String = "", // 2023-10-24 02:29:57
    val expiryDate: String = "", // 2023-11-23
)

/**
 * 구독 아이템 정보
 */
data class SubscribeItem(
    val id: String = "",
    val name: String = "",
)

/**
 * 유저 구독 상태
 *
 * @property subscriptionId 구독에 대한 유니크한 아이디
 * @property subscribeStatus 구독 상태
 * @property createdAt
 * @property expiryDate
 */
data class UserSubscribeStatus(
    val subscriptionId: Long = 0L,
    val subscribeStatus: SubscribeStatus = SubscribeStatus.Default,
    val expiryDate: String = "",
    val createdAt: String = "",
)

/**
 * @property Default 아무것도 구독하지 않은 상태
 * @property Subscribed 구독 중 상태
 */
enum class SubscribeStatus {
    Default, Subscribed
}
