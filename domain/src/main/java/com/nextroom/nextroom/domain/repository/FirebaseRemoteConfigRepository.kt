package com.nextroom.nextroom.domain.repository

import kotlinx.coroutines.flow.Flow

interface FirebaseRemoteConfigRepository {
    suspend fun getFirebaseRemoteConfigValue(key: String): Flow<String>

    companion object {
        const val REMOTE_KEY_APP_MIN_VERSION = "app_min_version"
        const val REMOTE_KEY_KAKAO_BUSINESS_CHANNEL_URL = "kakao_business_channel_url"
    }
}
