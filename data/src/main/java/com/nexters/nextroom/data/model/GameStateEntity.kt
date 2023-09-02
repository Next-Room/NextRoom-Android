package com.nexters.nextroom.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nexters.nextroom.data.model.GameStateEntity.Companion.GAME_STATE_TABLE
import com.nexters.nextroom.domain.model.GameState

@Entity(
    tableName = GAME_STATE_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = ThemeEntity::class,
            parentColumns = arrayOf("themeId"),
            childColumns = arrayOf("playingThemeId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class GameStateEntity(
    @PrimaryKey val playingThemeId: Int,
    val playing: Boolean,
    val timeLimit: Int,
    val lastSeconds: Int,
    val hintLimit: Int,
    val usedHints: Set<Int>,
    val stoppedTime: Long, // 게임이 중단된 시간
) {
    companion object {
        const val GAME_STATE_TABLE = "GameState"
    }
}

fun GameStateEntity.toDomain(): GameState {
    val calibratedTime =
        (lastSeconds - (System.currentTimeMillis() - stoppedTime) / 1000).coerceAtLeast(0).toInt()
    return GameState(
        playingThemeId = playingThemeId,
        playing = playing,
        timeLimit = timeLimit,
        lastSeconds = calibratedTime,
        hintLimit = hintLimit,
        usedHints = usedHints,
    )
}
