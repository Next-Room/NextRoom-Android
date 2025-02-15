package com.nextroom.nextroom.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nextroom.nextroom.data.model.GameStateEntity.Companion.GAME_STATE_TABLE
import com.nextroom.nextroom.domain.model.GameState
import com.nextroom.nextroom.domain.model.ThemeImageCustomInfo

@Entity(
    tableName = GAME_STATE_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = ThemeEntity::class,
            parentColumns = arrayOf("themeId"),
            childColumns = arrayOf("playingThemeId"),
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
)
data class GameStateEntity(
    @PrimaryKey val playingThemeId: Int,
    val timeLimitInMinute: Int,
    val hintLimit: Int,
    val usedHints: Set<Int>,
    val startTime: Long, // 게임을 시작한 시간
    val useTimerUrl: Boolean,
    val themeImageUrl: String? = null,
    val themeImageCustomInfo: ThemeImageCustomInfo? = null,
) {
    companion object {
        const val GAME_STATE_TABLE = "GameState"
    }
}

fun GameStateEntity.toDomain(): GameState {
    val calibratedTime = (timeLimitInMinute * 60 - (System.currentTimeMillis() - startTime) / 1000).coerceAtLeast(0).toInt()
    return GameState(
        playingThemeId = playingThemeId,
        timeLimitInMinute = timeLimitInMinute,
        lastSeconds = calibratedTime,
        hintLimit = hintLimit,
        usedHints = usedHints,
        startTime = startTime,
        useTimerUrl = useTimerUrl,
        themeImageUrl = themeImageUrl,
        themeImageCustomInfo = themeImageCustomInfo
    )
}
