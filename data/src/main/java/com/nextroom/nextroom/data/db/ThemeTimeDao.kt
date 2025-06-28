package com.nextroom.nextroom.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nextroom.nextroom.data.model.ThemeTimeEntity
import com.nextroom.nextroom.data.model.ThemeTimeEntity.Companion.THEME_TIME_TABLE_NAME

@Dao
interface ThemeTimeDao {

    @Query("UPDATE $THEME_TIME_TABLE_NAME SET recentPlayed = :playedAt WHERE themeId = :themeId")
    suspend fun updateRecentPlayed(themeId: Int, playedAt: Long)

    @Query("UPDATE $THEME_TIME_TABLE_NAME SET recentUpdated = :updatedAt WHERE themeId = :themeId")
    suspend fun updateRecentUpdated(themeId: Int, updatedAt: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeInfo(vararg themeTimeEntity: ThemeTimeEntity)

    @Query("DELETE FROM $THEME_TIME_TABLE_NAME WHERE themeId = :themeId")
    suspend fun deleteTimeInfo(themeId: Int)

    @Query("SELECT EXISTS(SELECT * FROM $THEME_TIME_TABLE_NAME WHERE themeId = :themeId)")
    suspend fun isTimeInfoExists(themeId: Int): Boolean
}
