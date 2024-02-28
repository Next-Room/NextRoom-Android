package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.domain.model.ThemeInfo

data class ThemeDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("timeLimit")
    val timeLimit: Int,
    @SerializedName("hintLimit")
    val hintLimit: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("modifiedAt")
    val modifiedAt: String,
)

fun ThemeDto.toDomain(hints: List<Hint> = emptyList()): ThemeInfo {
    return ThemeInfo(
        id = id,
        title = title,
        timeLimitInMinute = timeLimit,
        hintLimit = hintLimit,
        hints = hints,
    )
}

fun List<ThemeDto>.toDomain(): List<ThemeInfo> {
    return map { it.toDomain() }
}
