package com.nexters.nextroom.data.datasource

import com.nexters.nextroom.data.db.HintDao
import com.nexters.nextroom.data.db.ThemeDao
import com.nexters.nextroom.data.db.ThemeTimeDao
import com.nexters.nextroom.data.model.ThemeTimeEntity
import com.nexters.nextroom.data.model.toDomain
import com.nexters.nextroom.data.model.toEntity
import com.nexters.nextroom.domain.model.ThemeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeLocalDataSource @Inject constructor(
    private val themeDao: ThemeDao,
    private val themeTimeDao: ThemeTimeDao,
    private val hintDao: HintDao,
) {
    suspend fun getThemes(adminCode: String): Flow<List<ThemeInfo>> {
        return themeDao.getThemes(adminCode).map { themes ->
            themes.map { theme ->
                val hints = hintDao.getHints(theme.themeId)
                theme.toDomain(hints.map { it.toDomain() })
            }
        }
    }

    suspend fun updateThemes(adminCode: String, newThemes: List<ThemeInfo>) {
        val newData = newThemes.toEntity(adminCode).toTypedArray()
        themeDao.insertThemes(*newData)
    }

    suspend fun getTheme(themeId: Int): Flow<ThemeInfo> {
        return themeDao.getTheme(themeId).map {
            val hints = hintDao.getHints(it.themeId)
            it.toDomain(hints.toDomain())
        }
    }

    suspend fun getUpdatedInfo(themeId: Int): Long {
        return themeTimeDao.getTimeInfo(themeId)?.recentUpdated ?: 0L
    }

    suspend fun updateUpdatedInfo(themeId: Int, updatedAt: Long) {
        if (themeTimeDao.isTimeInfoExists(themeId)) {
            themeTimeDao.updateRecentUpdated(themeId, updatedAt)
        } else {
            themeTimeDao.insertTimeInfo(ThemeTimeEntity(themeId, recentUpdated = updatedAt))
        }
    }

    suspend fun updatePlayedInfo(themeId: Int, playedAt: Long) {
        if (themeTimeDao.isTimeInfoExists(themeId)) {
            themeTimeDao.updateRecentPlayed(themeId, playedAt)
        } else {
            themeTimeDao.insertTimeInfo(ThemeTimeEntity(themeId, recentPlayed = playedAt))
        }
    }
}
