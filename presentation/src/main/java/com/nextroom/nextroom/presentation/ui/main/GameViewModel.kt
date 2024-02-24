package com.nextroom.nextroom.presentation.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.TimerState
import com.nextroom.nextroom.domain.repository.GameStateRepository
import com.nextroom.nextroom.domain.repository.HintRepository
import com.nextroom.nextroom.domain.repository.StatisticsRepository
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.domain.repository.TimerRepository
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.model.Hint
import com.nextroom.nextroom.presentation.model.InputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val themeRepository: ThemeRepository,
    private val timerRepository: TimerRepository,
    private val gameStateRepository: GameStateRepository,
    private val hintRepository: HintRepository,
    private val statsRepository: StatisticsRepository,
) : BaseViewModel<GameScreenState, GameEvent>() {

    override val container: Container<GameScreenState, GameEvent> = container(GameScreenState())

    init {
        viewModelScope.launch {
            timerRepository.lastSeconds.collect(::tick)
        }

        viewModelScope.launch {
            timerRepository.timerState.collect {
                Timber.tag("MANGBAAM-GameViewModel)").d("timer state: $it")
                if (it == TimerState.Finished) {
                    finishGame()
                }
            }
        }
    }

    fun startOrResumeGame() = intent {
        gameStateRepository.getGameState()?.let { gameState -> // 정상적으로 게임 시작
            with(gameState) {
                setGameScreenState(
                    seconds = timeLimitInMinute * 60,
                    hintLimit = hintLimit,
                    usedHints = usedHints,
                    lastSeconds = lastSeconds,
                    startTime = startTime,
                )
            }
            startGame(gameState.lastSeconds)
        } ?: { // 게임 정보가 없는 경우 (= 게임 종료)
            viewModelScope.launch {
                postSideEffect(GameEvent.GameFinish)
            }
        }

        // 게임 시작 통계 집계 시작
//                statsRepository.recordGameStartStats(GameStats(it.id, DateTimeUtil().currentTime() ?: ""))
    }

    fun inputHintCode(key: Int) = intent {
        if (state.currentInput.length == 4) return@intent
        val newCode = state.currentInput + key.digitToChar()
        reduce {
            state.copy(
                currentInput = newCode,
                inputState = InputState.Typing,
            )
        }
        if (newCode.length == 4) validateHintCode()
    }

    fun backspaceHintCode() = intent {
        if (state.currentInput.isBlank()) {
            reduce { state.copy(inputState = InputState.Empty) }
            return@intent
        }
        reduce {
            val length = state.currentInput.length
            state.copy(
                currentInput = state.currentInput.dropLast(1),
                inputState = if (length <= 1) InputState.Empty else InputState.Typing,
            )
        }
    }

    fun clearHintCode() = intent {
        reduce { state.copy(currentInput = "", inputState = InputState.Empty) }
    }

    private fun validateHintCode() = intent {
        if (timerRepository.timerState.value is TimerState.Finished) {
            postSideEffect(GameEvent.GameFinish)
            return@intent
        }

        hintRepository.getHint(state.currentInput)?.let { hint ->
            reduce {
                state.copy(
                    usedHints = state.usedHints + hint.id,
                    inputState = InputState.Ok,
                )
            }
            postSideEffect(
                GameEvent.OnOpenHint(
                    Hint(
                        id = hint.id,
                        progress = hint.progress,
                        hint = hint.description,
                        answer = hint.answer,
                        answerOpened = state.answerOpenedHints.contains(hint.id),
                    ),
                ),
            )
            setGameState()
        } ?: run {
            reduce { state.copy(inputState = InputState.Error(R.string.game_wrong_hint_code)) }
            delay(500)
            reduce { state.copy(inputState = InputState.Typing, currentInput = "") }
        }
    }

    fun setGameState() = intent {
        if (timerRepository.timerState.value !is TimerState.Finished) {
            gameStateRepository.saveGameState(
                timeLimit = state.totalSeconds / 60,
                hintLimit = state.totalHintCount,
                usedHints = state.usedHints,
                startTime = state.startTime,
            )
        }
    }

    private fun startGame(seconds: Int) = intent {
        timerRepository.setTimer(seconds)
        startTimer()
    }

    fun finishGame(onFinished: () -> Unit = {}) = intent {
        viewModelScope.launch(Dispatchers.Main) {
            gameStateRepository.finishGame(onFinished)
        }
    }

    private fun tick(lastSeconds: Int) = intent {
        reduce { state.copy(lastSeconds = lastSeconds) }
    }

    private fun startTimer() {
        if (timerRepository.timerState.value == TimerState.Ready) {
            timerRepository.startTimer()
        }
    }

    private fun setGameScreenState(
        seconds: Int,
        hintLimit: Int,
        usedHints: Set<Int>,
        lastSeconds: Int,
        startTime: Long,
    ) = intent {
        reduce {
            state.copy(
                totalSeconds = seconds,
                totalHintCount = hintLimit,
                usedHints = usedHints,
                lastSeconds = lastSeconds,
                startTime = startTime,
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared: GameViewModel")
    }
}
