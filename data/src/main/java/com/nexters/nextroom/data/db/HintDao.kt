package com.nexters.nextroom.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nexters.nextroom.data.model.HintEntity
import com.nexters.nextroom.data.model.HintEntity.Companion.HINT_TABLE_NAME

@Dao
interface HintDao {
    @Query("SELECT * FROM $HINT_TABLE_NAME WHERE themeId = :themeId")
    suspend fun getHints(themeId: Int): List<HintEntity>

    @Query("SELECT * FROM $HINT_TABLE_NAME WHERE themeId = :themeId AND hintCode = :hintCode")
    suspend fun getHint(themeId: Int, hintCode: String): HintEntity?

    @Transaction
    suspend fun replaceHints(themeId: Int, hints: List<HintEntity>) {
        deleteHints(themeId)
        insertHints(*hints.toTypedArray())
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHints(vararg hintEntity: HintEntity)

    @Query("DELETE FROM $HINT_TABLE_NAME WHERE id = :themeId")
    suspend fun deleteHints(themeId: Int)
}
