package com.nexters.nextroom.data.repository

import com.nexters.nextroom.data.datasource.SettingDataSource
import com.nexters.nextroom.data.datasource.ThemeLocalDataSource
import com.nexters.nextroom.data.datasource.ThemeRemoteDataSource
import com.nexters.nextroom.domain.model.Result
import com.nexters.nextroom.domain.model.ThemeInfo
import com.nexters.nextroom.domain.model.suspendOnSuccess
import com.nexters.nextroom.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val settingDataSource: SettingDataSource,
    private val themeLocalDataSource: ThemeLocalDataSource,
    private val themeRemoteDateSource: ThemeRemoteDataSource,
) : ThemeRepository {

    override suspend fun getLocalThemes(): Flow<List<ThemeInfo>> {
        val adminCode = settingDataSource.getAdminCode()
        return themeLocalDataSource.getThemes(adminCode)
    }

    override suspend fun getThemes(): Result<List<ThemeInfo>> {
        val adminCode = settingDataSource.getAdminCode()
        // 로컬에 저장
        return themeRemoteDateSource.getThemes(adminCode).suspendOnSuccess {
            themeLocalDataSource.updateThemes(
                settingDataSource.getAdminCode(),
                it,
            )
        }
    }

    override suspend fun updateLatestTheme(themeId: Int) {
        settingDataSource.setLatestGameCode(themeId)
        themeLocalDataSource.updatePlayedInfo(themeId, System.currentTimeMillis())
    }

    override suspend fun getLatestTheme(): Flow<ThemeInfo> {
        val latestThemeId = settingDataSource.getLatestGameCode()
        return themeLocalDataSource.getTheme(latestThemeId)
    }

    override suspend fun getUpdatedInfo(themeId: Int): Long {
        return themeLocalDataSource.getUpdatedInfo(themeId)
    }
}
