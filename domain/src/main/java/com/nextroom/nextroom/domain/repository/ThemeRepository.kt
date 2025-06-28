package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.ThemeInfo
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun getLocalThemes(): Flow<List<ThemeInfo>> // 전체 테마 목록 호출
    suspend fun getThemes(): Result<List<ThemeInfo>>
    suspend fun upsertTheme(themeInfo: ThemeInfo)
    suspend fun updateLatestTheme(themeId: Int) // 최근 플레이 한 테마 갱신
    suspend fun getLatestTheme(): Flow<ThemeInfo> // 최근 플레이 한 테마
    suspend fun activateThemeBackgroundImage(activeThemeIdList: List<Int>, deActiveThemeIdList: List<Int>): Result<Unit>
    suspend fun getThemeById(id: Int): ThemeInfo
}
