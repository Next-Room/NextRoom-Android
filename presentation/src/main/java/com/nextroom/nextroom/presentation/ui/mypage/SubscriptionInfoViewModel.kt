package com.nextroom.nextroom.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionInfoViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchMyInfo()
    }

    private fun fetchMyInfo() {
        viewModelScope.launch {
            adminRepository.getUserSubscribe().onSuccess { mypage ->
                UiState.Loaded(
                    subscribeStatus = mypage.status,
                    startDate = mypage.startDate,
                    endDate = mypage.expiryDate,
                ).also {
                    _uiState.emit(it)
                }
            }.onFailure {
                _uiState.emit(UiState.Failure)
            }
        }
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Loaded(
            val subscribeStatus: SubscribeStatus,
            val startDate: String?,
            val endDate: String?,
        ) : UiState

        data object Failure : UiState
    }
}
