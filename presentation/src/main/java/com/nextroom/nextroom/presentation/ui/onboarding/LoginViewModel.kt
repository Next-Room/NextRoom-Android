package com.nextroom.nextroom.presentation.ui.onboarding

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
            } catch (e: Exception) {
                handleError(e)
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
                handleError(e)
            }
        }
    }

    sealed interface UIEvent {
        data class NeedAdditionalUserInfo(val shopName: String?) : UIEvent
    }
}