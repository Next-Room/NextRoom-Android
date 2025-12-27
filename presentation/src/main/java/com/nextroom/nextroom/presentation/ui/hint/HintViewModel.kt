package com.nextroom.nextroom.presentation.ui.hint

import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import com.nextroom.nextroom.domain.repository.TimerRepository
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.model.Hint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HintViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val dataStoreRepository: DataStoreRepository,
) : NewBaseViewModel() {

    private val _uiState = MutableStateFlow(HintState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HintEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    val lastSeconds = timerRepository.lastSeconds

    init {
        baseViewModelScope.launch {
            dataStoreRepository
                .getNetworkDisconnectedCount()
                .let {
                    updateNetworkDisconnectedCount(it)
                }
        }
    }

    private fun updateNetworkDisconnectedCount(count: Int) {
        _uiState.value = _uiState.value.copy(networkDisconnectedCount = count)
    }

    fun setHint(hint: Hint) {
        _uiState.value = _uiState.value.copy(
            hint = hint.copy(answerOpened = _uiState.value.hint.answerOpened)
        )
    }

    fun setSubscribeStatus(subscribeStatus: SubscribeStatus) {
        _uiState.value = _uiState.value.copy(userSubscribeStatus = subscribeStatus)
    }

    fun openAnswer() {
        _uiState.value = _uiState.value.copy(
            hint = _uiState.value.hint.copy(answerOpened = true)
        )
    }
}
