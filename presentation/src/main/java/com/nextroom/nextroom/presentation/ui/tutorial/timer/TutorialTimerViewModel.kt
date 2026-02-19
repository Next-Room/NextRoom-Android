package com.nextroom.nextroom.presentation.ui.tutorial.timer

import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.model.InputState
import com.nextroom.nextroom.presentation.ui.tutorial.TutorialData
import com.nextroom.nextroom.presentation.ui.tutorial.TutorialSharedViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TutorialTimerViewModel @AssistedInject constructor(
    @Assisted private val tutorialSharedViewModel: TutorialSharedViewModel
) : NewBaseViewModel() {

    private val _uiState = MutableStateFlow(TutorialTimerState())
    val uiState = combine(
        _uiState,
        tutorialSharedViewModel.state
    ) { state, sharedState ->
        state.copy(
            totalSeconds = sharedState.totalSeconds,
            lastSeconds = sharedState.lastSeconds,
            openedHintCount = sharedState.openedHintIds.size,
            totalHintCount = sharedState.totalHintCount
        )
    }.stateIn(baseViewModelScope, SharingStarted.Lazily, _uiState.value)

    private val _uiEvent = MutableSharedFlow<TutorialTimerEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        tutorialSharedViewModel.startTimer()
    }

    fun inputHintCode(key: Int) {
        val current = _uiState.value.currentInput
        if (current.length >= 4) return

        val newCode = current + key.toString()
        _uiState.update { it.copy(currentInput = newCode, inputState = InputState.Typing) }

        if (newCode.length == 4) {
            validateHintCode(newCode)
        }
    }

    fun backspaceHintCode() {
        val current = _uiState.value.currentInput
        if (current.isEmpty()) return

        _uiState.update {
            it.copy(
                currentInput = current.dropLast(1),
                inputState = if (current.length <= 1) InputState.Empty else InputState.Typing
            )
        }
    }

    private fun validateHintCode(code: String) {
        baseViewModelScope.launch {
            if (tutorialSharedViewModel.state.value.lastSeconds <= 0) {
                _uiEvent.emit(TutorialTimerEvent.TimerFinished)
                clearInput()
                return@launch
            }

            val hint = TutorialData.getRandomHint(code)
            if (hint != null) {
                _uiState.update { it.copy(inputState = InputState.Ok) }
                delay(200)
                _uiEvent.emit(TutorialTimerEvent.OpenHint(hint))
                clearInput()
            } else {
                _uiState.update { it.copy(inputState = InputState.Error(R.string.game_wrong_hint_code)) }
                delay(500)
                clearInput()
            }
        }
    }

    private fun clearInput() {
        _uiState.update { it.copy(currentInput = "", inputState = InputState.Empty) }
    }

    fun dismissTooltips() {
        _uiState.update { it.copy(showTooltips = false) }
    }

    fun exitTutorial() {
        tutorialSharedViewModel.finishTutorial()
        _uiEvent.tryEmit(TutorialTimerEvent.ExitTutorial)
    }

    @AssistedFactory
    interface Factory {
        fun create(tutorialSharedViewModel: TutorialSharedViewModel): TutorialTimerViewModel
    }
}
