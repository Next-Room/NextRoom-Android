package com.nextroom.nextroom.presentation.ui.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckPasswordViewModel @Inject constructor(
    val adminRepository: AdminRepository
) : ViewModel() {
    val inputPassword = MutableStateFlow("")
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            inputPassword.collect { password ->
                if (password.length == MAX_PASSWORD_LEN) {
                    checkPassword(inputPassword.value)
                }
            }
        }
    }

    fun onNumberClicked(number: String) {
        if (inputPassword.value.length == MAX_PASSWORD_LEN) return
        viewModelScope.launch {
            inputPassword.emit(inputPassword.value + number)
        }
    }

    private fun checkPassword(inputPassword: String) {
        viewModelScope.launch {
            if (inputPassword == adminRepository.getAppPassword()) {
                UiEvent.PasswordCorrect
            } else {
                clearPassword()
                UiEvent.PasswordInCorrect
            }.also {
                _uiEvent.emit(it)
            }
        }
    }

    private fun clearPassword() {
        viewModelScope.launch {
            inputPassword.emit("")
        }
    }

    fun onBackSpaceClicked() {
        if (inputPassword.value.isNotEmpty()) {
            viewModelScope.launch {
                inputPassword.emit(inputPassword.value.dropLast(1))
            }
        }
    }

    sealed interface UiEvent {
        data object PasswordCorrect : UiEvent
        data object PasswordInCorrect : UiEvent
    }

    companion object {
        const val MAX_PASSWORD_LEN = 4
    }
}