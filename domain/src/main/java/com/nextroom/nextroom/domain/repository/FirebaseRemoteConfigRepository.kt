package com.nextroom.nextroom.domain.repository

import kotlinx.coroutines.flow.Flow

interface FirebaseRemoteConfigRepository {
    suspend fun getFirebaseRemoteConfigValue(key: String): Flow<String>

    companion object {
        const val REMOTE_KEY_APP_MIN_VERSION = "app_min_version"

        /** 이 날짜(yyyy-MM-dd)부터 구독자만 게임을 시작할 수 있다 */
        const val REMOTE_KEY_SUBSCRIPTION_REQUIRED_DATE = "subscription_required_date"

        /** 구독 프로모션 대상자에게 프로모션을 노출할 확률(0.0 ~ 1.0) */
        const val REMOTE_KEY_SUBSCRIPTION_PROMOTION_PROBABILITY =
            "subscription_promotion_probability"
    }
}
