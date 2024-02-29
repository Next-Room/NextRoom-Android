package com.nextroom.nextroom.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import com.nextroom.nextroom.data.model.ThemeTimeEntity.Companion.THEME_TIME_TABLE_NAME

@Entity(
    tableName = THEME_TIME_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = ThemeEntity::class,
            parentColumns = arrayOf("themeId"),
            childColumns = arrayOf("themeId"),
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    primaryKeys = ["themeId", "recentUpdated"],
)
data class ThemeTimeEntity(
    val themeId: Int = 0,
    val recentUpdated: Long = 0L,
    val recentPlayed: Long = 0L,
) {
    companion object {
        const val THEME_TIME_TABLE_NAME = "ThemePlayTime"
    }
}
