package com.nextroom.nextroom.presentation.ui.hint

import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import com.nextroom.nextroom.domain.repository.TimerRepository
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.model.Hint
import com.nextroom.nextroom.presentation.ui.main.GameSharedViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HintViewModel @AssistedInject constructor(
    private val timerRepository: TimerRepository,
    private val dataStoreRepository: DataStoreRepository,
    @Assisted private val gameSharedViewModel: GameSharedViewModel
) : NewBaseViewModel() {

    private val _uiState = MutableStateFlow(HintState())
    val uiState = combine(
        _uiState,
        gameSharedViewModel.openedHintIds,
        gameSharedViewModel.openedAnswerIds
    ) { state, openedHintIds, openedAnswerIds ->
        state.copy(
            isHintOpened = state.hint.id in openedHintIds,
            isAnswerOpened = state.hint.id in openedAnswerIds
        )
    }.stateIn(
        baseViewModelScope,
        SharingStarted.Lazily,
        _uiState.value
    )

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

    @AssistedFactory
    interface Factory {
        fun create(gameSharedViewModel: GameSharedViewModel): HintViewModel
    }
}
