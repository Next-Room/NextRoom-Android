package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.GameState

interface GameStateRepository {

    suspend fun saveGameState(
        timeLimitInMinute: Int,
        hintLimit: Int,
        usedHints: Set<Int>,
        startTime: Long,
    )

    suspend fun finishGame(onFinished: () -> Unit = {})

    suspend fun getGameState(): GameState?
}
