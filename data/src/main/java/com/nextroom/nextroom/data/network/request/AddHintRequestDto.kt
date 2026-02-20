package com.nextroom.nextroom.data.network.request

import com.google.gson.annotations.SerializedName

data class AddHintRequestDto(
    @SerializedName("themeId") val themeId: Int,
    @SerializedName("hintCode") val hintCode: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("answer") val answer: String,
    @SerializedName("progress") val progress: Int,
)
