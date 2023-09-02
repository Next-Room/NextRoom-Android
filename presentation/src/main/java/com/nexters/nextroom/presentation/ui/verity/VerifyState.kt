package com.nexters.nextroom.presentation.ui.verity

import com.nexters.nextroom.presentation.model.InputState

data class VerifyState(
    val currentInput: String = "",
    val inputState: InputState = InputState.Empty,
)
