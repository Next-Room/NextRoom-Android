package com.nextroom.nextroom.domain.request

data class AddThemeRequest(
    val title: String,
    val timeLimit: Int,
    val hintLimit: Int,
)
