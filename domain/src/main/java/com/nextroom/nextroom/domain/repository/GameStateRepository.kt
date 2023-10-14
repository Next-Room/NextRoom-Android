package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.GameState

interface GameStateRepository {

    suspend fun saveGameState(
        playing: Boolean,
        timeLimit: Int,
        lastSeconds: Int,
        hintLimit: Int,
        usedHints: Set<Int>,
    )

    suspend fun finishGame(onFinished: () -> Unit = {})

    suspend fun getGameState(): GameState?
}
