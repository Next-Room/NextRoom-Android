package com.nextroom.nextroom.presentation.model

import java.io.Serializable

data class Hint(
    val id: Int = 0,
    val progress: Int = 0,
    val hint: String = "",
    val answer: String = "",
    val hintImageUrlList: List<String> = emptyList(),
    val answerImageUrlList: List<String> = emptyList()
) : Serializable
