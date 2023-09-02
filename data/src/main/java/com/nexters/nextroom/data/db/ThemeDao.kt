package com.nexters.nextroom.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nexters.nextroom.data.model.ThemeEntity
import com.nexters.nextroom.data.model.ThemeEntity.Companion.THEME_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface ThemeDao {

    @Query("SELECT * FROM $THEME_TABLE_NAME WHERE adminCode = :adminCode")
    fun getThemes(adminCode: String): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM $THEME_TABLE_NAME WHERE themeId = :themeId LIMIT 1")
    fun getTheme(themeId: Int): Flow<ThemeEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertThemes(vararg themes: ThemeEntity)

    @Query("DELETE FROM $THEME_TABLE_NAME WHERE adminCode = :adminCode")
    suspend fun deleteThemes(adminCode: String)
}
