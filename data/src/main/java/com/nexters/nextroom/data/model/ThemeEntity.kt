package com.nexters.nextroom.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexters.nextroom.data.model.ThemeEntity.Companion.THEME_TABLE_NAME
import com.nexters.nextroom.domain.model.Hint
import com.nexters.nextroom.domain.model.ThemeInfo

@Entity(tableName = THEME_TABLE_NAME)
data class ThemeEntity(
    @PrimaryKey val themeId: Int = 0,
    val adminCode: String = "00000",
    val title: String = "",
    val timeLimit: Int = 3600,
    val hintLimit: Int = -1,
) {
    companion object {
        const val THEME_TABLE_NAME = "Theme"
    }
}

fun ThemeEntity.toDomain(hints: List<Hint> = emptyList()): ThemeInfo {
    return ThemeInfo(
        id = themeId,
        title = title,
        timeLimit = timeLimit,
        hintLimit = hintLimit,
        hints = hints,
    )
}

fun List<ThemeEntity>.toDomain(): List<ThemeInfo> {
    return map { it.toDomain() }
}

fun ThemeInfo.toEntity(adminCode: String): ThemeEntity {
    return ThemeEntity(
        themeId = id,
        adminCode = adminCode,
        title = title,
        timeLimit = timeLimit * 60,
        hintLimit = hintLimit,
    )
}

fun List<ThemeInfo>.toEntity(adminCode: String): List<ThemeEntity> {
    return map { it.toEntity(adminCode) }
}
