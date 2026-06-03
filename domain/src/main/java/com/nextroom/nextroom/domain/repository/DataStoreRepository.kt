package com.nextroom.nextroom.domain.repository

interface DataStoreRepository {
    suspend fun getNetworkDisconnectedCount(): Int
    suspend fun setNetworkDisconnectedCount(count: Int)

    suspend fun isFirstInitOfDay(): Boolean

    suspend fun setRecommendBackgroundCustomDialogHidden(time: Long)
    suspend fun getRecommendBackgroundCustomDialogHiddenUntil(): Long

    suspend fun setHasSeenGuidePopup()
    suspend fun getHasSeenGuidePopup(): Boolean
}
