package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.data.db.GameStateDao
import com.nextroom.nextroom.data.model.GameStateEntity
import com.nextroom.nextroom.data.model.toDomain
import com.nextroom.nextroom.domain.model.GameState
import com.nextroom.nextroom.domain.model.ThemeImageCustomInfo
import com.nextroom.nextroom.domain.repository.GameStateRepository
import com.nextroom.nextroom.domain.repository.TimerRepository
import javax.inject.Inject

class GameStateRepositoryImpl @Inject constructor(
    private val settingDataSource: SettingDataSource,
    private val timerRepository: TimerRepository,
    private val gameStateDao: GameStateDao,
) : GameStateRepository {
    override suspend fun saveGameState(
        timeLimitInMinute: Int,
        hintLimit: Int,
        usedHints: Set<Int>,
        startTime: Long,
        themeImageUrl: String?,
        themeImageCustomInfo: ThemeImageCustomInfo?,
    ) {
        val themeId = settingDataSource.getLatestGameCode()
        gameStateDao.insertGameState(
            GameStateEntity(
                playingThemeId = themeId,
                timeLimitInMinute = timeLimitInMinute,
                hintLimit = hintLimit,
                usedHints = usedHints,
                startTime = startTime,
                themeImageUrl = themeImageUrl,
                themeImageCustomInfo = themeImageCustomInfo
            ),
        )
    }

    override suspend fun finishGame(onFinished: () -> Unit) {
        timerRepository.stopTimer()
        gameStateDao.deleteGameState()
        onFinished()
    }

    override suspend fun getGameState(): GameState? {
        return gameStateDao.getGameState()?.toDomain()
    }
}
