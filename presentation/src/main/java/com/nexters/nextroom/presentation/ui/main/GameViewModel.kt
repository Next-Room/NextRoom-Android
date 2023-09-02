package com.nexters.nextroom.presentation.ui.main

import androidx.lifecycle.viewModelScope
import com.nexters.nextroom.domain.model.TimerState
import com.nexters.nextroom.domain.repository.GameStateRepository
import com.nexters.nextroom.domain.repository.HintRepository
import com.nexters.nextroom.domain.repository.ThemeRepository
import com.nexters.nextroom.domain.repository.TimerRepository
import com.nexters.nextroom.presentation.R
import com.nexters.nextroom.presentation.base.BaseViewModel
import com.nexters.nextroom.presentation.model.InputState
import com.nexters.nextroom.presentation.ui.hint.HintState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val timerRepository: TimerRepository,
    private val gameStateRepository: GameStateRepository,
    private val hintRepository: HintRepository,
) : BaseViewModel<GameScreenState, Nothing>() {

    override val container: Container<GameScreenState, Nothing> = container(GameScreenState())

    init {
        viewModelScope.launch {
            // 강제 종료된 게임 가져오기
            gameStateRepository.getGameState()?.let { gameState ->
                setTimeLimit(gameState.timeLimit)
                setHintLimit(gameState.hintLimit)
                recoverUsedHints(gameState.usedHints)
                startGame(gameState.lastSeconds)
            } ?: themeRepository.getLatestTheme().collect { // 정상 종료된 경우
                setTimeLimit(it.timeLimit)
                setHintLimit(it.hintLimit)
                startGame(it.timeLimit)
            }
        }

        viewModelScope.launch {
            timerRepository.lastSeconds.collect(::tick)
        }

        viewModelScope.launch {
            timerRepository.timerState.collect {
                if (it == TimerState.Finished) {
                    finishGame()
                }
            }
        }
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
        hintRepository.getHint(state.currentInput)?.let { hint ->
            reduce {
                state.copy(
                    usedHints = state.usedHints + hint.id,
                    currentHint = HintState(
                        hintId = hint.id,
                        progress = hint.progress,
                        hint = hint.description,
                        answer = hint.answer,
                    ),
                    inputState = InputState.Ok,
                )
            }
        } ?: run {
            reduce { state.copy(inputState = InputState.Error(R.string.game_wrong_hint_code)) }
            delay(500)
            reduce { state.copy(inputState = InputState.Typing, currentInput = "") }
        }
    }

    private fun setGameState(
        timeLimit: Int,
        lastSeconds: Int,
        hintLimit: Int,
        usedHints: Set<Int>,
    ) = intent {
        gameStateRepository.saveGameState(
            playing = true,
            timeLimit = timeLimit,
            lastSeconds = lastSeconds,
            hintLimit = hintLimit,
            usedHints = usedHints,
        )
    }

    private fun startGame(initSeconds: Int) = intent {
        timerRepository.initTimer(initSeconds)
        startTimer()
    }

    fun finishGame(onFinished: () -> Unit = {}) = intent {
        viewModelScope.launch(Dispatchers.Main) {
            gameStateRepository.finishGame(onFinished)
        }
    }

    private fun tick(lastSeconds: Int) = intent {
        setGameState(
            state.totalSeconds,
            lastSeconds,
            state.totalHintCount,
            state.usedHints,
        )
        reduce { state.copy(lastSeconds = lastSeconds) }
    }

    private fun startTimer() {
        if (timerRepository.timerState.value == TimerState.Ready) {
            timerRepository.startTimer()
        }
    }

    private fun setTimeLimit(timeLimit: Int = 0) = intent {
        reduce { state.copy(totalSeconds = timeLimit) }
    }

    private fun setHintLimit(hintLimit: Int = -1) = intent {
        reduce { state.copy(totalHintCount = hintLimit) }
    }

    private fun recoverUsedHints(usedHints: Set<Int>) = intent {
        reduce { state.copy(usedHints = usedHints) }
    }

    fun openAnswer(hintId: Int) = intent {
        reduce { state.copy(answerOpenedHints = state.answerOpenedHints + hintId) }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared: GameViewModel")
    }
}
