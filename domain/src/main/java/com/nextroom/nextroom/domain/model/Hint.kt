package com.nextroom.nextroom.domain.model

import java.io.Serializable

data class Hint(
    val id: Int,
    val title: String = "",
    val code: String = "", // 힌트 코드
    val description: String = "", // 힌트
    val answer: String = "", // 정답
    val progress: Int = 0, // 진행률
    val hintImageUrlList: List<String>,
    val answerImageUrlList: List<String>,
) : Serializable
