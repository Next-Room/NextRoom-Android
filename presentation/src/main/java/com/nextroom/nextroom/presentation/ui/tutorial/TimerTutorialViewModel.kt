package com.nextroom.nextroom.presentation.ui.tutorial

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.repository.GameStateRepository
import com.nextroom.nextroom.domain.repository.HintRepository
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.domain.repository.TimerRepository
import com.nextroom.nextroom.presentation.model.InputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerTutorialViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val themeRepository: ThemeRepository,
    private val timerRepository: TimerRepository,
    private val gameStateRepository: GameStateRepository,
    private val hintRepository: HintRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            timerRepository.lastSeconds.collect(::tick)
        }
    }

    fun startGame() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            uiState.value.copy(
                totalSeconds = timeLimitInMinute * 60,
                totalHintCount = hintLimit,
                usedHints = emptySet(),
                lastSeconds = timeLimitInMinute * 60,
                startTime = startTime,
            ).also { _uiState.emit(it) }

            startGame(startTime + timeLimitInMinute * 60 * 1000)
        }
    }

    fun inputHintCode(key: Int) {
        viewModelScope.launch {
            if (uiState.value.currentInput.length == 4) return@launch
            val newCode = uiState.value.currentInput + key.digitToChar()
            uiState.value.copy(
                currentInput = newCode,
                inputState = InputState.Typing,
            ).also { _uiState.emit(it) }
            if (newCode.length == 4) openHint()
        }
    }

    fun backspaceHintCode() {
        viewModelScope.launch {
            if (uiState.value.currentInput.isBlank()) {
                uiState.value.copy(inputState = InputState.Empty).also {
                    _uiState.emit(it)
                }
                return@launch
            }
            val length = uiState.value.currentInput.length
            uiState.value.copy(
                currentInput = uiState.value.currentInput.dropLast(1),
                inputState = if (length <= 1) InputState.Empty else InputState.Typing,
            ).also { _uiState.emit(it) }
        }
    }

    fun clearHintCode() {
        viewModelScope.launch {
            uiState.value.copy(currentInput = "", inputState = InputState.Empty).also {
                _uiState.emit(it)
            }
            _uiEvent.emit(UiEvent.ClearHintCode)
        }
    }

    private suspend fun openHint() {
        uiState.value.copy(
            usedHints = uiState.value.usedHints + randomHintCode,
            inputState = InputState.Ok,
        ).also { _uiState.emit(it) }

        _uiEvent.emit(UiEvent.OnOpenHint)
    }

    private suspend fun startGame(endTimeMillis: Long) {
        timerRepository.startTimerUntil(endTimeMillis)
    }

//    fun finishGame(onFinished: () -> Unit = {}) = intent {
//        viewModelScope.launch(Dispatchers.Main) {
//            gameStateRepository.finishGame(onFinished)
//        }
//    }

    private suspend fun tick(lastSeconds: Int) {
        uiState.value.copy(lastSeconds = lastSeconds).also {
            _uiState.emit(it)
        }
    }

    override fun onCleared() {
        timerRepository.stopTimer()
        super.onCleared()
    }

    data class UiState(
        val totalSeconds: Int = 0,
        val lastSeconds: Int = 0,
        val currentInput: String = "",
        val inputState: InputState = InputState.Empty,
        val usedHints: Set<Int> = emptySet(),
        val answerOpenedHints: Set<Int> = emptySet(),
        val totalHintCount: Int = -1,
        val startTime: Long = -1,
    ) {
        val usedHintsCount: Int
            get() = usedHints.size
    }

    sealed interface UiEvent {
        data object OnOpenHint : UiEvent

        data object GameFinish : UiEvent
        data object ClearHintCode : UiEvent
    }


    companion object {
        const val timeLimitInMinute = 60
        const val hintLimit = 10
        const val randomHintCode = 1234
    }
}