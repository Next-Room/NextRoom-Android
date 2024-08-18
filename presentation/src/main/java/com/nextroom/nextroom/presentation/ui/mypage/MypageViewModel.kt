package com.nextroom.nextroom.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        fetchMyInfo()
    }

    fun logout() {
        viewModelScope.launch {
            adminRepository.logout()
        }
    }

    private fun fetchMyInfo() {
        viewModelScope.launch {
            adminRepository.getUserSubscribe().onSuccess { mypage ->
                UiState.Loaded(
                    shopName = mypage.name,
                    status = mypage.status,
                ).also {
                    _uiState.emit(it)
                }
            }.onFailure {
                _uiState.emit(UiState.Failure)
            }
        }
    }

    fun resign() {
        viewModelScope.launch {
            adminRepository.resign().onSuccess {
                _uiEvent.emit(UiEvent.ResignSuccess)
            }.onFailure {
                _uiEvent.emit(UiEvent.ResignFail)
            }
        }
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Loaded(
            val shopName: String,
            val status: SubscribeStatus,
        ) : UiState

        data object Failure : UiState
    }

    sealed interface UiEvent {
        data object ResignSuccess : UiEvent
        data object ResignFail : UiEvent
    }
}
