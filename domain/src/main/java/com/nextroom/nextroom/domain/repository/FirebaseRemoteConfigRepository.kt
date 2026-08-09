package com.nextroom.nextroom.domain.repository

import kotlinx.coroutines.flow.Flow

interface FirebaseRemoteConfigRepository {
    suspend fun getFirebaseRemoteConfigValue(key: String): Flow<String>

    companion object {
        const val REMOTE_KEY_APP_MIN_VERSION = "app_min_version"

        /** 이 날짜(yyyy-MM-dd)부터 구독자만 게임을 시작할 수 있다 */
        const val REMOTE_KEY_SUBSCRIPTION_REQUIRED_DATE = "subscription_required_date"
    }
}
