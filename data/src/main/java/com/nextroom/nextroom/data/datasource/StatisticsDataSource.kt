package com.nextroom.nextroom.data.datasource

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

    suspend fun recordHintStats(hintStats: HintStats) {
        statsDao.insertHintStatsForLastGame(hintStats.toEntity())
    }

    suspend fun postGameStats() {
        /*apiService.postGameStats().onSuspendSuccess {
            statsDao.deleteAllGameStats()
        }.onFailure {
            TODO API 나온 후 구현 필요
        }*/
    }
}
