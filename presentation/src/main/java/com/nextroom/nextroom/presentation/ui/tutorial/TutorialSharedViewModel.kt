package com.nextroom.nextroom.presentation.ui.tutorial

import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorialSharedViewModel @Inject constructor() : NewBaseViewModel() {

    private val _state = MutableStateFlow(TutorialSharedState())
    val state: StateFlow<TutorialSharedState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun startTimer(totalSeconds: Int = TutorialData.DEFAULT_TIME_LIMIT_SECONDS) {
        _state.update { it.copy(totalSeconds = totalSeconds, lastSeconds = totalSeconds) }

        timerJob?.cancel()
        timerJob = baseViewModelScope.launch {
            while (_state.value.lastSeconds > 0) {
                delay(1000)
                _state.update { it.copy(lastSeconds = it.lastSeconds - 1) }
            }
        }
    }

    fun modifyTime(newTotalMinutes: Int) {
        val newTotalSeconds = newTotalMinutes * 60
        startTimer(newTotalSeconds)
    }

    fun setCurrentHint(hint: TutorialHint) {
        _state.update { it.copy(currentHint = hint) }
    }

    fun addOpenedHintId(hintId: Int) {
        _state.update { it.copy(openedHintIds = it.openedHintIds + hintId) }
    }

    fun addOpenedAnswerId(hintId: Int) {
        _state.update { it.copy(openedAnswerIds = it.openedAnswerIds + hintId) }
    }

    fun finishTutorial() {
        timerJob?.cancel()
        _state.value = TutorialSharedState()
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}

data class TutorialSharedState(
    val totalSeconds: Int = TutorialData.DEFAULT_TIME_LIMIT_SECONDS,
    val lastSeconds: Int = TutorialData.DEFAULT_TIME_LIMIT_SECONDS,
    val currentHint: TutorialHint? = null,
    val openedHintIds: Set<Int> = emptySet(),
    val openedAnswerIds: Set<Int> = emptySet(),
    val totalHintCount: Int = 3
)
