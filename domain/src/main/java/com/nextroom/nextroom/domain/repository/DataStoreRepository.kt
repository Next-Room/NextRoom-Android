package com.nextroom.nextroom.domain.repository

interface DataStoreRepository {
    suspend fun getIsInitLaunch(): Boolean

    val isFirstInitOfDay: Boolean

    fun setRecommendBackgroundCustomDialogHidden(time: Long)
    fun getRecommendBackgroundCustomDialogHidden(): Long
}
