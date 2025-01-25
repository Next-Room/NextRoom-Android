package com.nextroom.nextroom.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nextroom.nextroom.data.model.ThemeEntity.Companion.THEME_TABLE_NAME
import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.domain.model.ThemeImageCustomInfo
import com.nextroom.nextroom.domain.model.ThemeInfo

@Entity(tableName = THEME_TABLE_NAME)
data class ThemeEntity(
    @PrimaryKey val themeId: Int = 0,
    val adminCode: String = "00000",
    val title: String = "",
    val timeLimitInMinute: Int = 60,
    val hintLimit: Int = -1,
    val themeImageCustomInfo: ThemeImageCustomInfo? = null
) {
    companion object {
        const val THEME_TABLE_NAME = "Theme"
    }
}

fun ThemeEntity.toDomain(hints: List<Hint> = emptyList()): ThemeInfo {
    return ThemeInfo(
        id = themeId,
        title = title,
        timeLimitInMinute = timeLimitInMinute,
        hintLimit = hintLimit,
        hints = hints,
        themeImageCustomInfo = themeImageCustomInfo
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
        timeLimitInMinute = timeLimitInMinute,
        hintLimit = hintLimit,
        themeImageCustomInfo = themeImageCustomInfo
    )
}

fun List<ThemeInfo>.toEntity(adminCode: String): List<ThemeEntity> {
    return map { it.toEntity(adminCode) }
}
