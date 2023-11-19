package com.nextroom.nextroom.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nextroom.nextroom.data.model.GameStatsEntity
import com.nextroom.nextroom.data.model.GameStatsEntity.Companion.GAME_STATS_TABLE
import com.nextroom.nextroom.data.model.HintStatsEntity
import com.nextroom.nextroom.data.model.HintStatsEntity.Companion.HINT_STATS_TABLE

@Dao
interface StatisticsDao {
    @Insert
    suspend fun insertGameStats(gameState: GameStatsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHintStats(hintStats: HintStatsEntity)

    @Query("SELECT * FROM $GAME_STATS_TABLE ORDER BY id DESC LIMIT 1")
    suspend fun getLastGameStats(): GameStatsEntity?

    @Query("SELECT * FROM $HINT_STATS_TABLE WHERE statsId = :statsId AND hintId = :hintId ORDER BY id DESC LIMIT 1")
    suspend fun getLastHintStats(statsId: Long, hintId: Int): HintStatsEntity?

    @Update
    suspend fun updateHintStats(hintStats: HintStatsEntity)

    @Query("SELECT * FROM $GAME_STATS_TABLE ORDER BY id")
    suspend fun getAllGameStats(): List<GameStatsEntity>

    @Query("DELETE FROM $GAME_STATS_TABLE")
    suspend fun deleteAllGameStats()

    @Delete
    suspend fun deleteGameStats(gameStats: GameStatsEntity)
}
