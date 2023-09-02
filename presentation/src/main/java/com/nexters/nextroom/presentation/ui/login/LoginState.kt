package com.nexters.nextroom.presentation.ui.login

import com.nexters.nextroom.presentation.model.InputState

data class LoginState(
    val currentIdInput: String = "",
    val idInputState: InputState = InputState.Empty,
    val currentPasswordInput: String = "",
    val passwordInputState: InputState = InputState.Empty,
)
