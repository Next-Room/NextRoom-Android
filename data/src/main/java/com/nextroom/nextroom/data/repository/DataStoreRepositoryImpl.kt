package com.nextroom.nextroom.data.repository

import com.mangbaam.commonutil.DateTimeUtil
import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val settingDataSource: SettingDataSource,
) : DataStoreRepository {
    override suspend fun getIsInitLaunch(): Boolean {
        return settingDataSource.getIsInitLaunch().also {
            if (it) settingDataSource.setIsNotInitLaunch()
        }
    }

    override val isFirstInitOfDay: Boolean
        get() = run {
            val util = DateTimeUtil()
            val pattern = "yyyy-MM-dd"
            util.longToDate(System.currentTimeMillis(), pattern) != util.longToDate(settingDataSource.getLastLaunchDate(), pattern)
        }.also { firstInit ->
            if (!firstInit) settingDataSource.setLastLaunchDate()
        }
}
