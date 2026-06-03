package com.nextroom.nextroom.data.repository

import com.mangbaam.commonutil.DateTimeUtil
import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import timber.log.Timber
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val settingDataSource: SettingDataSource,
) : DataStoreRepository {
    override suspend fun isFirstInitOfDay(): Boolean {
        val util = DateTimeUtil()
        val pattern = "yyyy-MM-dd"
        val today = util.longToDateString(System.currentTimeMillis(), pattern)
        val lastLaunchedDate = util.longToDateString(settingDataSource.getLastLaunchDate(), pattern)
        Timber.tag("MANGBAAM-DataStoreRepositoryImpl()").d("마지막 접속 일자: $lastLaunchedDate")
        val firstInit = today != lastLaunchedDate
        if (firstInit) settingDataSource.setLastLaunchDate()
        return firstInit
    }

    override suspend fun setRecommendBackgroundCustomDialogHidden(time: Long) {
        settingDataSource.setRecommendBackgroundCustomDialogHidden(time)
    }

    override suspend fun getRecommendBackgroundCustomDialogHiddenUntil(): Long {
        return settingDataSource.getRecommendBackgroundCustomDialogHiddenUntil()
    }

    override suspend fun getNetworkDisconnectedCount(): Int {
        return settingDataSource.getNetworkDisconnectedCount()
    }

    override suspend fun setNetworkDisconnectedCount(count: Int) {
        settingDataSource.setNetworkDisconnectedCount(count)
    }

    override suspend fun setHasSeenGuidePopup() {
        settingDataSource.saveHasSeenGuidePopup()
    }

    override suspend fun getHasSeenGuidePopup(): Boolean {
        return settingDataSource.getHasSeenGuidePopup()
    }
}
