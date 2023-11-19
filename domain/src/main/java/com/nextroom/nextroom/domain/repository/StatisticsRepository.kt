package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.statistics.GameStats
import com.nextroom.nextroom.domain.model.statistics.HintStats

interface StatisticsRepository {
    suspend fun recordGameStartStats(gameStats: GameStats)
    suspend fun recordHintStats(hintStats: HintStats)
    suspend fun recordAnswerOpenTime(hintId: Int, answerOpenTime: String)
    suspend fun postGameStats()
}
