package com.nextroom.nextroom.domain.repository

interface DataStoreRepository {
    suspend fun getIsInitLaunch(): Boolean

    suspend fun getNetworkDisconnectedCount(): Int
    fun setNetworkDisconnectedCount(count: Int)

    val isFirstInitOfDay: Boolean

    fun setRecommendBackgroundCustomDialogHidden(time: Long)
    fun getRecommendBackgroundCustomDialogHiddenUntil(): Long
}
