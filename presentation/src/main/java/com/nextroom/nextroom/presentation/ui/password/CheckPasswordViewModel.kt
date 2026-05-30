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
class CheckPasswordViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : NewBaseViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    fun onNumberClicked(number: Int) {
        val current = _uiState.value.input
        if (current.length >= MAX_PASSWORD_LEN) return

        val newInput = current + number.toString()
        _uiState.update { it.copy(input = newInput, inputState = InputState.Typing) }

        if (newInput.length == MAX_PASSWORD_LEN) {
            checkPassword(newInput)
        }
    }

    fun onBackSpaceClicked() {
        val current = _uiState.value.input
        if (current.isEmpty()) return
        _uiState.update {
            it.copy(
                input = current.dropLast(1),
                inputState = if (current.length <= 1) InputState.Empty else InputState.Typing
            )
        }
    }

    private fun checkPassword(input: String) {
        baseViewModelScope.launch {
            if (input == adminRepository.getAppPassword()) {
                _uiEvent.emit(UiEvent.PasswordCorrect)
            } else {
                _uiState.update {
                    it.copy(
                        inputState = InputState.Error(R.string.text_incorrect_password_error_message)
                    )
                }
                _uiEvent.emit(UiEvent.PasswordInCorrect)
                delay(ERROR_DISPLAY_MILLIS)
                _uiState.update { UiState() }
            }
        }
    }

    data class UiState(
        val input: String = "",
        val inputState: InputState = InputState.Empty,
    )

    sealed interface UiEvent {
        data object PasswordCorrect : UiEvent
        data object PasswordInCorrect : UiEvent
    }

    companion object {
        const val MAX_PASSWORD_LEN = 4
        private const val ERROR_DISPLAY_MILLIS = 500L
    }
}
