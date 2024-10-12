package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.Banner

data class BannerDto(
    @SerializedName("description")
    val description: String,
    @SerializedName("url")
    val imageUrl: String
)

fun BannerDto.toDomain() = Banner(
    description = description,
    linkUrl = imageUrl
)

fun List<BannerDto>.toDomain(): List<Banner> {
    return map { it.toDomain() }
}
