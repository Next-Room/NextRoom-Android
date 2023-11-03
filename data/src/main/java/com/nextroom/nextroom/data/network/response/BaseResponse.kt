package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T,
)

data class BaseListResponse<T>(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<T>,
)
