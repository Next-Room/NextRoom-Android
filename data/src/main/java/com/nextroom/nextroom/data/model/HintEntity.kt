package com.nextroom.nextroom.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nextroom.nextroom.data.model.HintEntity.Companion.HINT_TABLE_NAME
import com.nextroom.nextroom.domain.model.Hint

@Entity(
    tableName = HINT_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = ThemeEntity::class,
            parentColumns = arrayOf("themeId"),
            childColumns = arrayOf("themeId"),
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
)
data class HintEntity(
    @PrimaryKey val id: Int,
    val themeId: Int = 0,
    val hintCode: String = "", // 힌트 코드
    val progress: Int = 0, // 진행률
    val description: String = "", // 힌트
    val answer: String = "", // 정답
) {
    companion object {
        const val HINT_TABLE_NAME = "Hint"
    }
}

fun HintEntity.toDomain(): Hint {
    return Hint(
        id = this.id,
        code = this.hintCode,
        progress = this.progress,
        description = this.description,
        answer = this.answer,
    )
}

fun List<HintEntity>.toDomain(): List<Hint> {
    return this.map {
        it.toDomain()
    }
}

fun Hint.toHintEntity(themeId: Int): HintEntity {
    return HintEntity(
        id = this.id,
        hintCode = this.code,
        themeId = themeId,
        description = this.description,
        answer = this.answer,
        progress = this.progress,
    )
}
