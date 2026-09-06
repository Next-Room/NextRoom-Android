package com.nextroom.nextroom.domain.repository

interface DataStoreRepository {
    suspend fun isFirstInitOfDay(): Boolean

    suspend fun setHasSeenGuidePopup()
    suspend fun getHasSeenGuidePopup(): Boolean
}
