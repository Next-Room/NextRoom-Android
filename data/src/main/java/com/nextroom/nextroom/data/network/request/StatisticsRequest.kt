package com.nextroom.nextroom.data.network.request

data class StatisticsRequest(
    val themeId: Int,
    val gameStartTime: String,
    val hint: List<HintRequest>,
) {
    data class HintRequest(
        val id: Int,
        val entryTime: String,
        val answerOpenTime: String?,
    )
}
