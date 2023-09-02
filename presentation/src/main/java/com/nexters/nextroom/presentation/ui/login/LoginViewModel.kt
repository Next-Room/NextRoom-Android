package com.nexters.nextroom.presentation.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.nexters.nextroom.domain.model.Result
import com.nexters.nextroom.domain.model.onFailure
import com.nexters.nextroom.domain.model.onSuccess
import com.nexters.nextroom.domain.repository.AdminRepository
import com.nexters.nextroom.presentation.R
import com.nexters.nextroom.presentation.base.BaseViewModel
import com.nexters.nextroom.presentation.model.InputState
import com.nexters.nextroom.presentation.model.UiText
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
class LoginViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : BaseViewModel<LoginState, LoginEvent>() {

    override val container: Container<LoginState, LoginEvent> = container(LoginState())
    val loginState: StateFlow<Boolean> = adminRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false,
    )

    init {
        viewModelScope.launch {
            adminRepository.loggedIn.collect {
                if (it) verifySuccess() // 로그인 됐으면 테마 목록으로 이동
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

    fun forgotCode() = intent {
        postSideEffect(LoginEvent.ShowMessage(UiText("현재 지원되지 않는 기능입니다")))
    }

    fun complete() = intent {
        adminRepository.login(state.currentIdInput, state.currentPasswordInput)
            .onSuccess {
                verifySuccess()
            }.onFailure {
                reduce { state.copy(idInputState = InputState.Error(R.string.blank)) }
                when (it) {
                    is Result.Failure.HttpError -> event(LoginEvent.LoginFailed(it.message))
                    is Result.Failure.NetworkError -> showMessage(R.string.error_network)
                    else -> showMessage(R.string.error_something)
                }
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
        postSideEffect(LoginEvent.ShowMessage(UiText(message)))
    }

    private fun showMessage(@StringRes messageId: Int) = intent {
        postSideEffect(LoginEvent.ShowMessage(UiText(messageId)))
    }

    private fun event(event: LoginEvent) {
        when (event) {
            is LoginEvent.LoginFailed -> showMessage(event.message)
            else -> {}
        }
    }
}
