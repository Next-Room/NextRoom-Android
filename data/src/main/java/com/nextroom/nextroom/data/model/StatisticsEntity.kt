package com.nextroom.nextroom.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nextroom.nextroom.data.model.GameStatsEntity.Companion.GAME_STATS_TABLE
import com.nextroom.nextroom.data.model.HintStatsEntity.Companion.HINT_STATS_TABLE
import com.nextroom.nextroom.domain.model.statistics.GameStats
import com.nextroom.nextroom.domain.model.statistics.HintStats

@Entity(tableName = GAME_STATS_TABLE)
data class GameStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val themeId: Int,
    val startTime: String,
) {
    companion object {
        const val GAME_STATS_TABLE = "GameStatisticsTable"
    }
}

fun GameStats.toEntity(): GameStatsEntity {
    return GameStatsEntity(
        themeId = themeId,
        startTime = startTime,
    )
}

@Entity(
    tableName = HINT_STATS_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = GameStatsEntity::class,
            parentColumns = ["id"],
            childColumns = ["statsId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class HintStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hintId: Int,
    val hintOpenTime: String,
    val answerOpenTime: String?,
) {
    var statsId: Long = 0

    companion object {
        const val HINT_STATS_TABLE = "HintStatisticsTable"
    }
}

fun HintStats.toEntity(): HintStatsEntity {
    return HintStatsEntity(
        hintId = hintId,
        hintOpenTime = hintOpenTime,
        answerOpenTime = answerOpenTime,
    )
}
