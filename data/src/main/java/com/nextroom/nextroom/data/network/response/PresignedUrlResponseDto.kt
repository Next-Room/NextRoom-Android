package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName

data class PresignedUrlResponseDto(
    @SerializedName("hintImageUrlList") val hintImageUrlList: List<String>?,
    @SerializedName("answerImageUrlList") val answerImageUrlList: List<String>?,
)
