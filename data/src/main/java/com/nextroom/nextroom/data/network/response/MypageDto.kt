package com.nextroom.nextroom.data.network.response

import com.google.gson.annotations.SerializedName
import com.nextroom.nextroom.domain.model.Mypage
import com.nextroom.nextroom.domain.model.SubscribeStatus

// TODO JH: 이름 변경 고려해보기
data class MypageDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String, // xx이스케이프 xx점
    @SerializedName("status") val status: String, // FREE or SUBSCRIPTION
    @SerializedName("startDate") val startDate: String?, // 2023-10-24 02:29:57
    @SerializedName("expiryDate") val expiryDate: String?, // 2023-11-23
    @SerializedName("createdAt") val createdAt: String, // 2023-11-23
) {
    fun toDomain(): Mypage {
        return Mypage(
            id = id,
            name = name,
            status = SubscribeStatus.ofValue(status),
            startDate = startDate,
            expiryDate = expiryDate,
            createdAt = createdAt,
        )
    }
}
