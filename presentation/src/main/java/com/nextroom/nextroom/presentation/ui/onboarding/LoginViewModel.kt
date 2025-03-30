package com.nextroom.nextroom.presentation.ui.onboarding

import com.nextroom.nextroom.domain.model.GoogleAuthResponse
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val adminRepository: AdminRepository,
) : NewBaseViewModel() {
    val apiLoading = MutableStateFlow(false)

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun requestGoogleAuth() {
        baseViewModelScope.launch {
            try {
                apiLoading.emit(true)
                adminRepository.requestGoogleAuth().getOrThrow
                    .let { UIEvent.GoogleAuthSuccess(it) }
                    .also { _uiEvent.emit(it) }
            } catch (e: Exception) {
                _uiEvent.emit(UIEvent.GoogleAuthFailed)
            } finally {
                apiLoading.emit(false)
            }
        }
    }

    sealed interface UIEvent {
        data class GoogleAuthSuccess(val googleAuthResponse: GoogleAuthResponse) : UIEvent
        data object GoogleAuthFailed : UIEvent
    }
}