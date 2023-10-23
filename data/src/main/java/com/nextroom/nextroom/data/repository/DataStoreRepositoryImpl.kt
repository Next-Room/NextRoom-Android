package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val settingDataSource: SettingDataSource,
) : DataStoreRepository {
    override suspend fun getIsInitLaunch(): Boolean {
        return settingDataSource.getIsInitLaunch()
    }

    override suspend fun setIsNotInitLaunch() {
        settingDataSource.setIsNotInitLaunch()
    }
}
