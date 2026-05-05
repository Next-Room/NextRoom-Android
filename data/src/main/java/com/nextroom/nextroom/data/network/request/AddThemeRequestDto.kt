package com.nextroom.nextroom.data.network.request

import com.google.gson.annotations.SerializedName

data class AddThemeRequestDto(
    @SerializedName("title") val title: String,
    @SerializedName("timeLimit") val timeLimit: Int,
    @SerializedName("hintLimit") val hintLimit: Int,
)
