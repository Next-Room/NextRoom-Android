package com.nextroom.nextroom.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nextroom.nextroom.data.model.GameStateEntity
import com.nextroom.nextroom.data.model.GameStateEntity.Companion.GAME_STATE_TABLE

@Dao
interface GameStateDao {
    @Query("SELECT * FROM $GAME_STATE_TABLE")
    suspend fun getGameState(): GameStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameState(gameStateEntity: GameStateEntity)

    @Query("DELETE FROM $GAME_STATE_TABLE")
    suspend fun deleteGameState()
}
