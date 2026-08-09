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
import kotlinx.coroutines.flow.asStateFlow
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

    private val _apiLoading = MutableStateFlow(false)
    val apiLoading: StateFlow<Boolean> = _apiLoading.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    /**
     * 계정 선택 UI가 뜨기 전까지도 로딩을 노출하기 위해, 자격증명을 요청하는 Fragment가 로딩 시작/해제를 알린다.
     */
    fun setGoogleAuthLoading(loading: Boolean) {
        _apiLoading.value = loading
    }

    /**
     * 구글 계정 선택은 Activity가 필요하므로 Fragment가 담당하고, 여기서는 발급받은 id token으로 로그인만 처리한다.
     */
    fun loginWithGoogle(idToken: String) {
        baseViewModelScope.launch {
            try {
                _apiLoading.emit(true)
                adminRepository.postGoogleLogin(idToken).getOrThrow.let {
                    if (!it.isComplete) {
                        _uiEvent.emit(UIEvent.NeedAdditionalUserInfo(it.shopName))
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _apiLoading.emit(false)
            }
        }
    }

    sealed interface UIEvent {
        data class NeedAdditionalUserInfo(val shopName: String?) : UIEvent
    }
}