package com.nextroom.nextroom.data.repository

import com.mangbaam.commonutil.DateTimeUtil
import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import timber.log.Timber
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
            val today = util.longToDateString(System.currentTimeMillis(), pattern)
            val lastLaunchedDate = util.longToDateString(settingDataSource.getLastLaunchDate(), pattern)
            Timber.tag("MANGBAAM-DataStoreRepositoryImpl()").d("마지막 접속 일자: $lastLaunchedDate")
            today != lastLaunchedDate
        }.also { firstInit ->
            if (firstInit) settingDataSource.setLastLaunchDate()
        }

    override fun setRecommendBackgroundCustomDialogHidden(time: Long) {
        settingDataSource.setRecommendBackgroundCustomDialogHidden(time)
    }

    override fun getRecommendBackgroundCustomDialogHiddenUntil(): Long {
        return settingDataSource.getRecommendBackgroundCustomDialogHiddenUntil()
    }

    override suspend fun getNetworkDisconnectedCount(): Int {
        return settingDataSource.getNetworkDisconnectedCount()
    }

    override fun setNetworkDisconnectedCount(count: Int) {
        settingDataSource.setNetworkDisconnectedCount(count)
    }
}
