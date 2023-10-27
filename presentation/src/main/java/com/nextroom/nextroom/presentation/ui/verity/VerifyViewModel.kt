package com.nextroom.nextroom.presentation.ui.verity

import androidx.annotation.StringRes
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.model.InputState
import com.nextroom.nextroom.presentation.model.UiText
import com.nextroom.nextroom.presentation.ui.login.LoginEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : BaseViewModel<VerifyState, LoginEvent>() { // TODO: LoginEvent 사용 하지 말고 따로 Event 분리 하기
    override val container: Container<VerifyState, LoginEvent> = container(VerifyState())

    fun inputCode(code: String) = intent {
        reduce {
            state.copy(
                currentInput = code,
                inputState = if (code.isEmpty()) InputState.Empty else InputState.Typing,
            )
        }
    }

    fun forgotCode() = intent {
        showMessage("현재 지원되지 않는 기능입니다")
    }

    fun complete() = intent {
        val success = adminRepository.verifyAdminCode(state.currentInput)
        reduce {
            state.copy(inputState = if (success) InputState.Ok else InputState.Error(R.string.blank))
        }
    }

    private fun showMessage(message: String) = intent {
        postSideEffect(LoginEvent.ShowMessage(UiText(message)))
    }

    private fun showMessage(@StringRes messageId: Int) = intent {
        postSideEffect(LoginEvent.ShowMessage(UiText(messageId)))
    }
}
