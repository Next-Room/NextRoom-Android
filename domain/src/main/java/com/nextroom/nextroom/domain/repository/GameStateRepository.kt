package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.GameState
import com.nextroom.nextroom.domain.model.ThemeImageCustomInfo

interface GameStateRepository {

    suspend fun saveGameState(
        timeLimitInMinute: Int,
        hintLimit: Int,
        usedHints: Set<Int>,
        startTime: Long,
        useTimerUrl: Boolean,
        themeImageUrl: String? = null,
        themeImageCustomInfo: ThemeImageCustomInfo? = null,
    )

    suspend fun finishGame(onFinished: () -> Unit = {})

    suspend fun getGameState(): GameState?

    suspend fun updateUsedHints(usedHints: Set<Int>)
}
