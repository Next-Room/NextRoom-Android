package com.nexters.nextroom.domain.repository

import com.nexters.nextroom.domain.model.GameState

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
