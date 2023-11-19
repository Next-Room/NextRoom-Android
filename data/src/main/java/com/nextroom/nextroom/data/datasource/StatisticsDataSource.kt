package com.nextroom.nextroom.data.datasource

import androidx.room.Transaction
import com.nextroom.nextroom.data.db.StatisticsDao
import com.nextroom.nextroom.data.model.toEntity
import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.domain.model.statistics.GameStats
import com.nextroom.nextroom.domain.model.statistics.HintStats
import javax.inject.Inject

class StatisticsDataSource @Inject constructor(
    private val statsDao: StatisticsDao,
    private val apiService: ApiService,
) {
    suspend fun recordGameStartStats(gameStats: GameStats) {
        statsDao.insertGameStats(gameStats.toEntity())
    }

    @Transaction
    suspend fun recordHintStats(hintStats: HintStats) {
        statsDao.getLastGameStats()?.let { lastGameStats ->
            statsDao.insertHintStats(
                hintStats.toEntity().apply {
                    statsId = lastGameStats.id
                },
            )
        }
    }

    @Transaction
    suspend fun updateAnswerOpenTime(hintId: Int, answerOpenTime: String) {
        val lastGameStats = statsDao.getLastGameStats() ?: return
        val lastHintStats = statsDao.getLastHintStats(lastGameStats.id, hintId) ?: return

        statsDao.updateHintStats(lastHintStats.copy(answerOpenTime = answerOpenTime).apply { statsId = lastGameStats.id })
    }

    suspend fun postGameStats() {
        /*apiService.postGameStats().onSuspendSuccess {
            statsDao.deleteAllGameStats()
        }.onFailure {
            TODO API 나온 후 구현 필요
        }*/
    }
}
