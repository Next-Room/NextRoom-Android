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
        return themeRemoteDateSource.getThemes().suspendOnSuccess { themes ->
            themes
                .map {
                    if (themeLocalDataSource.isThemeExist(it.id)) it.copy(themeImageCustomInfo = getThemeById(it.id).themeImageCustomInfo)
                    else it
                }
                .also {
                    themeLocalDataSource.updateThemes(
                        settingDataSource.getAdminCode(),
                        it,
                    )
                }
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

    override suspend fun activateThemeBackgroundImage(activeThemeIdList: List<Int>, deActiveThemeIdList: List<Int>): Result<Unit> {
        return themeRemoteDateSource.putActiveThemeBackgroundImage(ThemeBackgroundActivationId(activeThemeIdList, deActiveThemeIdList))
    }

    override suspend fun getThemeById(id: Int): ThemeInfo {
        return themeLocalDataSource.getTheme(id).first()
    }
}
