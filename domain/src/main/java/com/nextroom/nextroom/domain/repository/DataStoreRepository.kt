package com.nextroom.nextroom.domain.repository

interface DataStoreRepository {
    suspend fun getIsInitLaunch(): Boolean
    suspend fun setIsNotInitLaunch()
}
