package com.nextroom.nextroom.presentation.ui.login

import com.nextroom.nextroom.presentation.model.InputState

data class LoginState(
    val loading: Boolean = false,
    val currentIdInput: String = "",
    val idInputState: InputState = InputState.Empty,
    val currentPasswordInput: String = "",
    val passwordInputState: InputState = InputState.Empty,
    val idSaveChecked: Boolean = false
)
