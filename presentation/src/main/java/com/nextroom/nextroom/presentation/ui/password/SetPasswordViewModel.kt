package com.nextroom.nextroom.presentation.ui.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetPasswordViewModel @Inject constructor(
    val adminRepository: AdminRepository
) : ViewModel() {
    private val firstPassword = MutableStateFlow("")
    private val secondPassword = MutableStateFlow("")
    private val step = MutableStateFlow<UiState.Step>(UiState.Step.PasswordSetting)

    val uiState = combine(
        firstPassword,
        secondPassword,
        step,
    ) { firstPassword, secondPassword, step ->
        when (step) {
            UiState.Step.PasswordSetting -> firstPassword
            UiState.Step.PasswordConfirm -> secondPassword
        }.let { password ->
            UiState(displayPassword = password, step = step)
        }
    }
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            firstPassword.collect { password ->
                if (password.length == MAX_PASSWORD_LEN) {
                    step.emit(UiState.Step.PasswordConfirm)
                }
            }
        }
        viewModelScope.launch {
            secondPassword.collect { password ->
                if (password.length == MAX_PASSWORD_LEN) {
                    if (isPasswordMatch()) {
                        savePassword(password)
                        _uiEvent.emit(UiEvent.SettingPasswordFinished)
                    } else {
                        secondPassword.emit("")
                        _uiEvent.emit(UiEvent.PasswordNotMatched)
                    }
                }
            }
        }
    }

    fun onNumberClicked(number: String) {
        viewModelScope.launch {
            when (step.value) {
                UiState.Step.PasswordSetting -> {
                    if (firstPassword.value.length == MAX_PASSWORD_LEN) return@launch
                    firstPassword.emit(firstPassword.value + number)
                }

                UiState.Step.PasswordConfirm -> {
                    if (secondPassword.value.length == MAX_PASSWORD_LEN) return@launch
                    secondPassword.emit(secondPassword.value + number)
                }
            }
        }
    }

    private fun isPasswordMatch() = firstPassword.value == secondPassword.value

    private suspend fun savePassword(password: String) {
        adminRepository.saveAppPassword(password)
    }

    fun onBackSpaceClicked() {
        viewModelScope.launch {
            when (step.value) {
                UiState.Step.PasswordSetting -> {
                    if (firstPassword.value.isNotEmpty()) {
                        firstPassword.emit(firstPassword.value.dropLast(1))
                    }
                }

                UiState.Step.PasswordConfirm -> {
                    if (secondPassword.value.isNotEmpty()) {
                        secondPassword.emit(secondPassword.value.dropLast(1))
                    }
                }
            }
        }
    }

    data class UiState(
        val displayPassword: String,
        val step: Step,
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
    }
}