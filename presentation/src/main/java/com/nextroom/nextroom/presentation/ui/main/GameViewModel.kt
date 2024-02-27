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
        startOrResumeGame()

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

    private fun startOrResumeGame() {
        viewModelScope.launch {
            gameStateRepository.getGameState()?.let { gameState -> // 강제 종료된 게임 가져오기
                Timber.tag("MANGBAAM-GameViewModel(startGame)").d("강제종료 게임 다시 시작")
                setTimeLimit(gameState.timeLimit)
                setHintLimit(gameState.hintLimit)
                recoverUsedHints(gameState.usedHints)
                startGame(gameState.lastSeconds)
            } ?: themeRepository.getLatestTheme().collect { // 정상 종료된 경우
                Timber.tag("MANGBAAM-GameViewModel(startGame)").d("정상종료 게임 시작")
                setTimeLimit(it.timeLimit)
                setHintLimit(it.hintLimit)
                val overflowTime = ((savedStateHandle.get<Long>("overflowTime") ?: 0L) / 1000L).toInt().also {
                    Timber.tag("MANGBAAM-GameViewModel(startGame)").d("overflow: $it")
                }
                startGame(it.timeLimit - overflowTime)

                // 게임 시작 통계 집계 시작
//                statsRepository.recordGameStartStats(GameStats(it.id, DateTimeUtil().currentTime() ?: ""))
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
        suspend fun openHint(hint: com.nextroom.nextroom.domain.model.Hint) {
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
        }

        hintRepository.getHint(state.currentInput)?.let { hint ->
            if (state.usedHints.contains(hint.id)) {
                openHint(hint)
            } else if (state.usedHintsCount < state.totalHintCount) {
                openHint(hint)
            } else {
                postSideEffect(GameEvent.ShowAvailableHintExceedError)
                reduce { state.copy(inputState = InputState.Typing, currentInput = "") }
            }
        } ?: run {
            // TODO: 현재는 사용하는 곳이 없어 문제가 없지만, state가 중복으로 수집되어 사용될 수 있음. 수정 필요
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

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared: GameViewModel")
    }
}
