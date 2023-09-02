package com.nexters.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nexters.nextroom.domain.model.Hint

data class HintDto(
    @SerializedName("answer")
    val answer: String,
    @SerializedName("contents")
    val contents: String,
    @SerializedName("hintCode")
    val hintCode: String,
    @SerializedName("hintTitle")
    val hintTitle: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("progress")
    val progress: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("modifiedAt")
    val modifiedAt: String,
)

fun HintDto.toDomain(): Hint {
    return Hint(
        id = id,
        code = hintCode,
        progress = progress,
        description = contents,
        answer = answer,
    )
}

fun List<HintDto>.toDomain(): List<Hint> {
    return map { it.toDomain() }
}
