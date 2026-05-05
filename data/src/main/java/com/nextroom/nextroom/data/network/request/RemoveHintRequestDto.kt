package com.nextroom.nextroom.data.network.request

import com.google.gson.annotations.SerializedName

data class RemoveHintRequestDto(
    @SerializedName("id") val id: Int,
)
