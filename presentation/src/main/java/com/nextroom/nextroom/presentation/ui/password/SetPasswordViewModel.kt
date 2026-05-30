package com.nextroom.nextroom.presentation.ui.password

import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.model.InputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetPasswordViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : NewBaseViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    private var firstPassword: String = ""

    fun onNumberClicked(number: Int) {
        val current = _uiState.value.displayPassword
        if (current.length >= MAX_PASSWORD_LEN) return

        val newInput = current + number.toString()
        _uiState.update { it.copy(displayPassword = newInput, inputState = InputState.Typing) }

        if (newInput.length == MAX_PASSWORD_LEN) {
            onPasswordEntered(newInput)
        }
    }

    fun onBackSpaceClicked() {
        val current = _uiState.value.displayPassword
        if (current.isEmpty()) return
        _uiState.update {
            it.copy(
                displayPassword = current.dropLast(1),
                inputState = if (current.length <= 1) InputState.Empty else InputState.Typing
            )
        }
    }

    private fun onPasswordEntered(password: String) {
        baseViewModelScope.launch {
            when (_uiState.value.step) {
                UiState.Step.PasswordSetting -> {
                    firstPassword = password
                    _uiState.update {
                        UiState(step = UiState.Step.PasswordConfirm)
                    }
                }

                UiState.Step.PasswordConfirm -> {
                    if (password == firstPassword) {
                        adminRepository.saveAppPassword(password)
                        _uiEvent.emit(UiEvent.SettingPasswordFinished)
                    } else {
                        _uiState.update {
                            it.copy(
                                inputState = InputState.Error(
                                    R.string.text_incorrect_password_error_message
                                )
                            )
                        }
                        _uiEvent.emit(UiEvent.PasswordNotMatched)
                        delay(ERROR_DISPLAY_MILLIS)
                        _uiState.update {
                            it.copy(displayPassword = "", inputState = InputState.Empty)
                        }
                    }
                }
            }
        }
    }

    data class UiState(
        val displayPassword: String = "",
        val step: Step = Step.PasswordSetting,
        val inputState: InputState = InputState.Empty,
    ) {
        sealed interface Step {
            data object PasswordSetting : Step
            data object PasswordConfirm : Step
        }
    }

    sealed interface UiEvent {
        data object PasswordNotMatched : UiEvent
        data object SettingPasswordFinished : UiEvent
    }

    companion object {
        const val MAX_PASSWORD_LEN = 4
        private const val ERROR_DISPLAY_MILLIS = 500L
    }
}
