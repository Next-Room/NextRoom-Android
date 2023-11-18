package com.nextroom.nextroom.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.nextroom.nextroom.data.model.GameStatsEntity
import com.nextroom.nextroom.data.model.GameStatsEntity.Companion.GAME_STATS_TABLE
import com.nextroom.nextroom.data.model.HintStatsEntity

@Dao
interface StatisticsDao {
    @Insert
    suspend fun insertGameStats(gameState: GameStatsEntity)

    @Insert
    suspend fun insertHintStats(hintStats: HintStatsEntity)

    @Query("SELECT * FROM $GAME_STATS_TABLE ORDER BY id DESC LIMIT 1")
    suspend fun getLastGameStats(): GameStatsEntity?

    @Transaction
    suspend fun insertHintStatsForLastGame(hintStats: HintStatsEntity) {
        getLastGameStats()?.let { lastGameStats ->
            insertHintStats(
                hintStats.apply {
                    statsId = lastGameStats.id
                },
            )
        }
    }

    @Query("SELECT * FROM $GAME_STATS_TABLE ORDER BY id")
    suspend fun getAllGameStats(): List<GameStatsEntity>

    @Query("DELETE FROM $GAME_STATS_TABLE")
    suspend fun deleteAllGameStats()

    @Delete
    suspend fun deleteGameStats(gameStats: GameStatsEntity)
}
