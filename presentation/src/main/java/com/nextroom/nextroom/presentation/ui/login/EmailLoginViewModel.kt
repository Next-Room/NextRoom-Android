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
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.model.InputState
import com.nextroom.nextroom.presentation.model.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class EmailLoginViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : BaseViewModel<EmailLoginState, EmailLoginEvent>() {

    override val container: Container<EmailLoginState, EmailLoginEvent> = container(EmailLoginState())
    val loginState: StateFlow<Boolean> = adminRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false,
    )

    private var idSaveChecked = false

    init {
        baseViewModelScope.launch {
            adminRepository.loggedIn.collect {
                if (it) verifySuccess() // 로그인 됐으면 테마 목록으로 이동
            }
        }
    }

    fun checkEmailSaved() {
        baseViewModelScope.launch {
            intent {
                val idSaveChecked = adminRepository.getEmailSaveChecked()
                val userEmail = if (idSaveChecked) adminRepository.getUserEmail() else ""
                reduce {
                    state.copy(
                        idSaveChecked = idSaveChecked,
                        userEmail = userEmail
                    )
                }
            }
        }
    }

    fun inputCode(code: String) = intent {
        reduce {
            state.copy(
                currentIdInput = code,
                idInputState = if (code.isNotBlank()) InputState.Typing else InputState.Empty,
            )
        }
    }

    fun inputPassword(pw: String) = intent {
        reduce {
            state.copy(
                currentPasswordInput = pw,
                passwordInputState = if (pw.isNotBlank()) InputState.Typing else InputState.Empty,
            )
        }
    }

    fun onIdSaveChecked(checked: Boolean) {
        idSaveChecked = checked
    }

    fun complete() = intent {
        reduce { state.copy(loading = true) }
        adminRepository.login(state.currentIdInput, state.currentPasswordInput, idSaveChecked)
            .onSuccess {
                verifySuccess()
            }.onFailure {
                reduce { state.copy(idInputState = InputState.Error(R.string.blank)) }
                when (it) {
                    is Result.Failure.HttpError -> event(EmailLoginEvent.EmailLoginFailed(it.message))
                    is Result.Failure.NetworkError -> showMessage(R.string.error_network)
                    else -> showMessage(R.string.error_something)
                }
            }.onFinally {
                reduce { state.copy(loading = false) }
            }
    }

    fun initState() = intent {
        reduce {
            state.copy(idInputState = InputState.Empty, passwordInputState = InputState.Empty)
        }
    }

    private fun verifySuccess() = intent {
        reduce {
            state.copy(idInputState = InputState.Ok, passwordInputState = InputState.Ok)
        }
    }

    private fun showMessage(message: String) = intent {
        postSideEffect(EmailLoginEvent.ShowMessage(UiText(message)))
    }

    private fun showMessage(@StringRes messageId: Int) = intent {
        postSideEffect(EmailLoginEvent.ShowMessage(UiText(messageId)))
    }

    private fun event(event: EmailLoginEvent) {
        when (event) {
            is EmailLoginEvent.EmailLoginFailed -> showMessage(event.message)
            else -> {}
        }
    }

    fun requestGoogleAuth() = intent {
        baseViewModelScope.launch {
            reduce { state.copy(loading = true) }
            try {
                adminRepository.requestGoogleAuth().getOrThrow
                    .also { postGoogleLogin(it.idToken) }
            } catch (e: GetCredentialCancellationException) {
                // do nothing
            } catch (e: Exception) {
                postSideEffect(EmailLoginEvent.GoogleAuthFailed)
            }
            reduce { state.copy(loading = false) }
        }
    }

    private fun postGoogleLogin(idToken: String) = intent {
        baseViewModelScope.launch {
            try {
                adminRepository.postGoogleLogin(idToken).getOrThrow.let {
                    if (!it.isComplete) {
                        postSideEffect(EmailLoginEvent.NeedAdditionalUserInfo(it.shopName))
                    }
                }
            } catch (e: Exception) {
                postSideEffect(EmailLoginEvent.GoogleLoginFailed)
            }
        }
    }
}
