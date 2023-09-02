package com.nexters.nextroom.data.repository

import com.nexters.nextroom.data.datasource.SettingDataSource
import com.nexters.nextroom.data.db.GameStateDao
import com.nexters.nextroom.data.model.GameStateEntity
import com.nexters.nextroom.data.model.toDomain
import com.nexters.nextroom.domain.model.GameState
import com.nexters.nextroom.domain.repository.GameStateRepository
import com.nexters.nextroom.domain.repository.TimerRepository
import javax.inject.Inject

class GameStateRepositoryImpl @Inject constructor(
    private val settingDataSource: SettingDataSource,
    private val timerRepository: TimerRepository,
    private val gameStateDao: GameStateDao,
) : GameStateRepository {
    override suspend fun saveGameState(playing: Boolean, timeLimit: Int, lastSeconds: Int, hintLimit: Int, usedHints: Set<Int>) {
        val themeId = settingDataSource.getLatestGameCode()
        if (playing) {
            gameStateDao.insertGameState(
                GameStateEntity(
                    playingThemeId = themeId,
                    playing = true,
                    timeLimit = timeLimit,
                    lastSeconds = lastSeconds,
                    hintLimit = hintLimit,
                    usedHints = usedHints,
                    stoppedTime = System.currentTimeMillis(),
                ),
            )
        } else {
            finishGame()
        }
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
