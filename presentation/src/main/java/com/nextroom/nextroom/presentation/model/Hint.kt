package com.nextroom.nextroom.presentation.model

import java.io.Serializable

data class Hint(
    val id: Int = -1,
    val progress: Int = 0,
    val hint: String = "",
    val answer: String = "",
    val answerOpened: Boolean = false,
    val hintImageUrlList: List<String> = emptyList(),
    val answerImageUrlList: List<String> = emptyList()
) : Serializable
