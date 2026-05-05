package com.nextroom.nextroom.domain.request

data class AddHintRequest(
    val themeId: Int,
    val hintCode: String,
    val contents: String,
    val answer: String,
    val progress: Int,
    val hintImageUrlList: List<String> = emptyList(),
    val answerImageUrlList: List<String> = emptyList(),
)
