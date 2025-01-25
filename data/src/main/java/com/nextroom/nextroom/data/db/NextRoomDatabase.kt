package com.nextroom.nextroom.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nextroom.nextroom.data.model.GameStateEntity
import com.nextroom.nextroom.data.model.GameStatsEntity
import com.nextroom.nextroom.data.model.HintEntity
import com.nextroom.nextroom.data.model.HintStatsEntity
import com.nextroom.nextroom.data.model.ThemeEntity
import com.nextroom.nextroom.data.model.ThemeTimeEntity

@Database(
    entities = [ThemeEntity::class, ThemeTimeEntity::class, HintEntity::class, GameStateEntity::class, GameStatsEntity::class, HintStatsEntity::class],
    version = 6,
)
@TypeConverters(TypeConverter::class)
abstract class NextRoomDatabase : RoomDatabase() {
    abstract fun themeDao(): ThemeDao
    abstract fun themeTimeDao(): ThemeTimeDao
    abstract fun hintDao(): HintDao
    abstract fun gameStateDao(): GameStateDao
    abstract fun statisticsDao(): StatisticsDao

    companion object {
        const val DB_NAME = "NEXT_ROOM_DB"
    }
}
