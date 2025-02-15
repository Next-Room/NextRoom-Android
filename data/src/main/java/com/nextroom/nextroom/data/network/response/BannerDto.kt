package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.Banner

data class BannerDto(
    @SerializedName("description")
    val description: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("url")
    val linkUrl: String
)

fun BannerDto.toDomain() = Banner(
    description = description,
    imageUrl = imageUrl,
    linkUrl = linkUrl
)

fun List<BannerDto>.toDomain(): List<Banner> {
    return map { it.toDomain() }
}
