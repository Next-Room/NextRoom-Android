package com.nextroom.nextroom.domain.repository

interface DataStoreRepository {
    suspend fun isFirstInitOfDay(): Boolean

    suspend fun setRecommendBackgroundCustomDialogHidden(time: Long)
    suspend fun getRecommendBackgroundCustomDialogHiddenUntil(): Long

    suspend fun setHasSeenGuidePopup()
    suspend fun getHasSeenGuidePopup(): Boolean
}
