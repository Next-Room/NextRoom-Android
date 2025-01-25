package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.data.datasource.ThemeLocalDataSource
import com.nextroom.nextroom.data.datasource.ThemeRemoteDataSource
import com.nextroom.nextroom.data.model.ThemeBackgroundActivationId
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.ThemeInfo
import com.nextroom.nextroom.domain.model.suspendOnSuccess
import com.nextroom.nextroom.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
        // 로컬에 저장
        return themeRemoteDateSource.getThemes().suspendOnSuccess {
            themeLocalDataSource.updateThemes(
                settingDataSource.getAdminCode(),
                it,
            )
        }
    }

    override suspend fun upsertTheme(themeInfo: ThemeInfo) {
        themeLocalDataSource.upsertTheme(
            adminCode = settingDataSource.getAdminCode(),
            themeInfo = themeInfo,
        )
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

    override suspend fun activateThemeBackgroundImage(activeThemeIdList: List<Int>, deActiveThemeIdList: List<Int>): Result<Unit> {
        return themeRemoteDateSource.putActiveThemeBackgroundImage(ThemeBackgroundActivationId(activeThemeIdList, deActiveThemeIdList))
    }

    override suspend fun getThemeById(id: Int): ThemeInfo {
        return themeLocalDataSource.getTheme(id).first()
    }
}
