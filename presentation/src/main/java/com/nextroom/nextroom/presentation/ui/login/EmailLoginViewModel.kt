package com.nextroom.nextroom.presentation.ui.login

import androidx.annotation.StringRes
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onFinally
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.model.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailLoginViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : NewBaseViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    val loginState: StateFlow<Boolean> = adminRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false,
    )

    private var emailSaveChecked = false

    init {
        checkEmailSaved()
    }

    private fun checkEmailSaved() {
        baseViewModelScope.launch {
            val saved = adminRepository.getEmailSaveChecked()
            val userEmail = if (saved) adminRepository.getUserEmail() else ""
            emailSaveChecked = saved
            _uiState.update {
                it.copy(emailSaveChecked = saved, currentEmailInput = userEmail)
            }
        }
    }

    fun inputEmail(email: String) {
        _uiState.update { it.copy(currentEmailInput = email) }
    }

    fun inputPassword(password: String) {
        _uiState.update { it.copy(currentPasswordInput = password, hasError = false) }
    }

    fun onEmailSaveChecked(checked: Boolean) {
        emailSaveChecked = checked
        _uiState.update { it.copy(emailSaveChecked = checked) }
    }

    fun complete() {
        val state = _uiState.value
        baseViewModelScope.launch {
            _uiState.update { it.copy(loading = true, hasError = false) }
            adminRepository.login(
                state.currentEmailInput,
                state.currentPasswordInput,
                emailSaveChecked
            )
                .onSuccess {
                    // loggedIn flow handles navigation
                }.onFailure {
                    _uiState.update { current -> current.copy(hasError = true) }
                    when (it) {
                        is Result.Failure.HttpError -> _uiEvent.emit(UiEvent.EmailLoginFailed(it.message))
                        is Result.Failure.NetworkError -> showMessage(R.string.error_network)
                        else -> showMessage(R.string.error_something)
                    }
                }.onFinally {
                    _uiState.update { it.copy(loading = false) }
                }
        }
    }

    fun requestGoogleAuth() {
        baseViewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            try {
                adminRepository.requestGoogleAuth().getOrThrow
                    .also { postGoogleLogin(it.idToken) }
            } catch (e: GetCredentialCancellationException) {
                // do nothing
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.GoogleAuthFailed)
            }
            _uiState.update { it.copy(loading = false) }
        }
    }

    private fun postGoogleLogin(idToken: String) {
        baseViewModelScope.launch {
            try {
                adminRepository.postGoogleLogin(idToken).getOrThrow.let {
                    if (!it.isComplete) {
                        _uiEvent.emit(UiEvent.NeedAdditionalUserInfo(it.shopName))
                    }
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.GoogleLoginFailed)
            }
        }
    }

    private suspend fun showMessage(@StringRes messageId: Int) {
        _uiEvent.emit(UiEvent.ShowMessage(UiText(messageId)))
    }

    data class UiState(
        val loading: Boolean = false,
        val currentEmailInput: String = "",
        val currentPasswordInput: String = "",
        val emailSaveChecked: Boolean = false,
        val hasError: Boolean = false,
    )

    sealed interface UiEvent {
        data class EmailLoginFailed(val message: String) : UiEvent
        data class ShowMessage(val message: UiText) : UiEvent
        data object GoogleAuthFailed : UiEvent
        data object GoogleLoginFailed : UiEvent
        data class NeedAdditionalUserInfo(val shopName: String?) : UiEvent
    }
}
