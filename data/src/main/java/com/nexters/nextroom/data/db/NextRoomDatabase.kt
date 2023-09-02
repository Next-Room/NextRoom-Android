package com.nexters.nextroom.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nexters.nextroom.data.model.GameStateEntity
import com.nexters.nextroom.data.model.HintEntity
import com.nexters.nextroom.data.model.ThemeEntity
import com.nexters.nextroom.data.model.ThemeTimeEntity

@Database(
    entities = [ThemeEntity::class, ThemeTimeEntity::class, HintEntity::class, GameStateEntity::class],
    version = 1,
)
@TypeConverters(TypeConverter::class)
abstract class NextRoomDatabase : RoomDatabase() {
    abstract fun themeDao(): ThemeDao
    abstract fun themeTimeDao(): ThemeTimeDao
    abstract fun hintDao(): HintDao
    abstract fun gameStateDao(): GameStateDao

    companion object {
        const val DB_NAME = "NEXT_ROOM_DB"
    }
}
