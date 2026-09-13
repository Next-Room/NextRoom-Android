package com.nextroom.nextroom.data.datasource

import androidx.room.withTransaction
import com.nextroom.nextroom.data.db.GameStateDao
import com.nextroom.nextroom.data.db.HintDao
import com.nextroom.nextroom.data.db.NextRoomDatabase
import com.nextroom.nextroom.data.db.ThemeDao
import com.nextroom.nextroom.data.db.ThemeTimeDao
import com.nextroom.nextroom.data.model.ThemeTimeEntity
import com.nextroom.nextroom.data.model.toDomain
import com.nextroom.nextroom.data.model.toEntity
import com.nextroom.nextroom.domain.model.ThemeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeLocalDataSource @Inject constructor(
    private val database: NextRoomDatabase,
    private val themeDao: ThemeDao,
    private val themeTimeDao: ThemeTimeDao,
    private val hintDao: HintDao,
    private val gameStateDao: GameStateDao,
) {
    suspend fun getThemes(): Flow<List<ThemeInfo>> {
        return themeDao.getThemes().map { themes ->
            themes.map { theme ->
                val hints = hintDao.getHints(theme.themeId)
                theme.toDomain(hints.map { it.toDomain() })
            }
        }
    }

    suspend fun updateThemes(newThemes: List<ThemeInfo>) {
        val newData = newThemes.toEntity().toTypedArray()
        themeDao.insertThemes(*newData)
    }

    suspend fun upsertTheme(themeInfo: ThemeInfo) {
        themeDao.insertTheme(themeInfo.toEntity())
    }

    suspend fun getTheme(themeId: Int): Flow<ThemeInfo> {
        return themeDao.getTheme(themeId).map {
            val hints = hintDao.getHints(it.themeId)
            it.toDomain(hints.toDomain())
        }
    }

    suspend fun isThemeExist(themeId: Int): Boolean {
        return themeDao.isThemeExist(themeId)
    }

    suspend fun updateUpdatedInfo(themeId: Int, updatedAt: Long) {
        if (themeTimeDao.isTimeInfoExists(themeId)) {
            themeTimeDao.updateRecentUpdated(themeId, updatedAt)
        } else {
            themeTimeDao.insertTimeInfo(ThemeTimeEntity(themeId, recentUpdated = updatedAt))
        }
    }

    /**
     * 로그아웃 시 이전 계정의 테마가 다음 계정에 노출되지 않도록 테마 캐시를 비운다.
     * Hint, ThemePlayTime, GameState 는 Theme 을 참조하지만 onDelete 가 NO_ACTION 이므로
     * FOREIGN KEY constraint 를 피하려면 Theme 보다 먼저 직접 지워야 한다.
     * 일부만 지워진 상태가 남지 않도록 트랜잭션으로 묶는다.
     */
    suspend fun clearThemes() {
        database.withTransaction {
            hintDao.deleteAllHints()
            themeTimeDao.deleteAllTimeInfo()
            gameStateDao.deleteGameState()
            themeDao.deleteAllThemes()
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
