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
    val subscribeStatus: SubscribeStatus = SubscribeStatus.None,
    val expiryDate: String = "",
    val createdAt: String = "",
)

/**
 * @property None 아무것도 아닌 상태 (현재 사용하지 않음)
 * @property Free 무료 체험
 * @property Hold 유예 기간 (무료 체험 끝)
 * @property Expiration 유예 기간 만료
 * @property Subscription 구독
 * @property SubscriptionExpiration 구독 만료
 */
enum class SubscribeStatus {
    None, Free, Hold, Expiration, Subscription, SubscriptionExpiration
}
