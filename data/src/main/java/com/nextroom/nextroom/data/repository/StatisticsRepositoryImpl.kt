package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.datasource.StatisticsDataSource
import com.nextroom.nextroom.domain.model.statistics.GameStats
import com.nextroom.nextroom.domain.model.statistics.HintStats
import com.nextroom.nextroom.domain.repository.StatisticsRepository
import timber.log.Timber
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val dataSource: StatisticsDataSource,
) : StatisticsRepository {

    override suspend fun recordGameStartStats(gameStats: GameStats) {
        dataSource.recordGameStartStats(gameStats)
    }

    override suspend fun recordHintStats(hintStats: HintStats) {
        dataSource.recordHintStats(hintStats)
    }

    override suspend fun recordAnswerOpenTime(hintId: Int, answerOpenTime: String) {
        dataSource.updateAnswerOpenTime(hintId, answerOpenTime)
    }

    override suspend fun postGameStats() {
        Timber.tag("MANGBAAM-StatisticsRepositoryImpl(postGameStats)").d("게임 통계 전송 시도")
        dataSource.postGameStats()
    }
}
