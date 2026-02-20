package com.nextroom.nextroom.domain.request

data class EditThemeRequest(
    val id: Int,
    val title: String,
    val timeLimit: Int,
    val hintLimit: Int,
)
