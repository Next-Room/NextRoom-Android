package com.nextroom.nextroom.presentation.ui.verity

import com.nextroom.nextroom.presentation.model.InputState

data class VerifyState(
    val currentInput: String = "",
    val inputState: InputState = InputState.Empty,
)
