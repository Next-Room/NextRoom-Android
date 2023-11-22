package com.nextroom.nextroom.data.datasource

import androidx.room.Transaction
import com.nextroom.nextroom.data.db.StatisticsDao
import com.nextroom.nextroom.data.model.toEntity
import com.nextroom.nextroom.data.model.toRequest
import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.request.StatisticsRequest
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.statistics.GameStats
import com.nextroom.nextroom.domain.model.statistics.HintStats
import com.nextroom.nextroom.domain.model.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
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

        statsDao.updateHintStats(
            lastHintStats.copy(answerOpenTime = answerOpenTime).apply { statsId = lastGameStats.id },
        )
    }

    suspend fun postGameStats() {
        statsDao.getAllGameStats().forEach { gameStats ->
            withContext(Dispatchers.IO) {
                val hints = statsDao.getHintStatsForGame(gameStats.id)
                val request = StatisticsRequest(
                    themeId = gameStats.themeId,
                    gameStartTime = gameStats.startTime,
                    hint = hints.map { it.toRequest() },
                )
                apiService.postGameStats(request).suspendOnSuccess {
                    Timber.tag("MANGBAAM-StatisticsDataSource(postGameStats)").d("통계 전송 성공")
                    statsDao.deleteGameStats(gameStats)
                }.onFailure {
                    Timber.tag("MANGBAAM-StatisticsDataSource(postGameStats)").d("통계 전송 실패: $it")
                }
            }
        }
    }
}
