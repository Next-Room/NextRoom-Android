package com.nextroom.nextroom.presentation.ui.main

import androidx.lifecycle.SavedStateHandle
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.repository.GameStateRepository
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.model.Hint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameSharedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gameStateRepository: GameStateRepository
) : NewBaseViewModel() {

    private val _state = MutableStateFlow(
        GameSharedState(
            subscribeStatus = TimerFragmentArgs.fromSavedStateHandle(savedStateHandle).subscribeStatus
        )
    )
    val state: StateFlow<GameSharedState> = _state.asStateFlow()

    fun setCurrentHint(hint: Hint) {
        _state.update { it.copy(currentHint = hint) }
    }

    fun setTotalHintCount(count: Int) {
        _state.update { it.copy(totalHintCount = count) }
    }

    fun addOpenedHintId(hintId: Int) {
        _state.update { it.copy(openedHintIds = it.openedHintIds + hintId) }
        baseViewModelScope.launch {
            gameStateRepository.updateUsedHints(_state.value.openedHintIds)
        }
    }

    fun updateOpenedHintIds(hintIds: Set<Int>) {
        _state.update { it.copy(openedHintIds = hintIds) }
    }

    fun getOpenedHintCount() = state.value.openedHintIds.size

    fun addOpenedAnswerId(hintId: Int) {
        _state.update { it.copy(openedAnswerIds = it.openedAnswerIds + hintId) }
    }
}

data class GameSharedState(
    val subscribeStatus: SubscribeStatus,
    val currentHint: Hint? = null,
    val openedHintIds: Set<Int> = emptySet(),
    val openedAnswerIds: Set<Int> = emptySet(),
    val totalHintCount: Int = -1
)