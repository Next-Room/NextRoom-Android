package com.nextroom.nextroom.presentation.ui.onboarding

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val adminRepository: AdminRepository,
) : NewBaseViewModel() {
    val loginState: StateFlow<Boolean> = adminRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false,
    )

    val apiLoading = MutableStateFlow(false)

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun requestGoogleAuth() {
        baseViewModelScope.launch {
            try {
                apiLoading.emit(true)
                adminRepository.requestGoogleAuth().getOrThrow
                    .also { postGoogleLogin(it.idToken) }
            } catch (e: GetCredentialCancellationException) {
                // do nothing
            } catch (e: Exception) {
                _uiEvent.emit(UIEvent.GoogleAuthFailed)
            } finally {
                apiLoading.emit(false)
            }
        }
    }

    private fun postGoogleLogin(idToken: String) {
        baseViewModelScope.launch {
            try {
                adminRepository.postGoogleLogin(idToken).getOrThrow.let {
                    if (!it.isComplete) {
                        _uiEvent.emit(UIEvent.NeedAdditionalUserInfo(it.shopName))
                    }
                }
            } catch (e: Exception) {
                _uiEvent.emit(UIEvent.GoogleLoginFailed)
            }
        }
    }

    sealed interface UIEvent {
        data object GoogleAuthFailed : UIEvent
        data object GoogleLoginFailed : UIEvent
        data class NeedAdditionalUserInfo(val shopName: String?) : UIEvent
    }
}