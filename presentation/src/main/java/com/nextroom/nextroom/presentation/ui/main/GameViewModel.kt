package com.nextroom.nextroom.presentation.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.GameState
import com.nextroom.nextroom.domain.model.ThemeImageCustomInfo
import com.nextroom.nextroom.domain.model.ThemeInfo
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
import kotlinx.coroutines.flow.first
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
            val latestGame = gameStateRepository.getGameState()
            fun isNewGame() = latestGame == null
            if (isNewGame()) {
                showGameBackgroundImage(loadLatestTheme())
                showGameStartConfirmDialog()
            } else {
                resumeGame(latestGame ?: return@launch)
            }
        }
    }

    private fun showGameBackgroundImage(theme: ThemeInfo) = intent {
        reduce {
            state.copy(
                themeImageUrl = theme.themeImageUrl,
                themeImageCustomInfo = theme.themeImageCustomInfo,
                themeImageEnabled = theme.useTimerUrl
            )
        }
    }

    private fun showGameStartConfirmDialog() = intent {
        postSideEffect(GameEvent.NewGame)
    }

    fun onGameStartClicked() {
        startOrResumeGame()
    }

    private suspend fun loadLatestTheme(): ThemeInfo {
        return themeRepository.getLatestTheme().first()
    }

    private fun startOrResumeGame() {
        viewModelScope.launch {
            gameStateRepository.getGameState()?.let { gameState -> // 비정상 종료된 게임이 존재하는 경우
                resumeGame(gameState)
            } ?: run { // 새로운 게임을 시작하는 경우
                with(themeRepository.getLatestTheme().first()) {
                    val startTime = System.currentTimeMillis()
                    gameStateRepository.saveGameState(
                        timeLimitInMinute = timeLimitInMinute,
                        hintLimit = hintLimit,
                        usedHints = emptySet(),
                        startTime = System.currentTimeMillis(),
                        themeImageUrl = themeImageUrl,
                        themeImageCustomInfo = themeImageCustomInfo
                    )
                    setGameScreenState(
                        seconds = timeLimitInMinute * 60,
                        hintLimit = hintLimit,
                        usedHints = emptySet(),
                        lastSeconds = timeLimitInMinute * 60,
                        startTime = startTime,
                        themeImageUrl = themeImageUrl,
                        themeImageCustomInfo = themeImageCustomInfo
                    )
                    startGame(startTime + timeLimitInMinute * 60 * 1000)
                }
            }
        }

        // 게임 시작 통계 집계 시작
//                statsRepository.recordGameStartStats(GameStats(it.id, DateTimeUtil().currentTime() ?: ""))
    }

    private fun resumeGame(gameState: GameState) {
        with(gameState) {
            setGameScreenState(
                seconds = timeLimitInMinute * 60,
                hintLimit = hintLimit,
                usedHints = usedHints,
                lastSeconds = lastSeconds,
                startTime = startTime,
                themeImageUrl = themeImageUrl,
                themeImageCustomInfo = themeImageCustomInfo
            )
            startGame(startTime + timeLimitInMinute * 60 * 1000)
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
        postSideEffect(GameEvent.ClearHintCode)
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
                        hintImageUrlList = hint.hintImageUrlList.toList(),
                        answerImageUrlList = hint.answerImageUrlList.toList()
                    ),
                    GameFragmentArgs.fromSavedStateHandle(savedStateHandle).subscribeStatus
                ),
            )
            setGameState()
        }

        if (timerRepository.timerState.value is TimerState.Finished) {
            postSideEffect(GameEvent.GameFinish)
            delay(500)
            clearHintCode()
            return@intent
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
            reduce { state.copy(inputState = InputState.Error(R.string.game_wrong_hint_code)) }
            delay(500)
            clearHintCode()
        }
    }

    private fun setGameState() = intent {
        if (timerRepository.timerState.value !is TimerState.Finished) {
            gameStateRepository.saveGameState(
                timeLimitInMinute = state.totalSeconds / 60,
                hintLimit = state.totalHintCount,
                usedHints = state.usedHints,
                startTime = state.startTime,
                themeImageUrl = state.themeImageUrl,
                themeImageCustomInfo = state.themeImageCustomInfo
            )
        }
    }

    private fun startGame(endTimeMillis: Long) = intent {
        timerRepository.startTimerUntil(endTimeMillis)
    }

    fun finishGame(onFinished: () -> Unit = {}) = intent {
        viewModelScope.launch(Dispatchers.Main) {
            gameStateRepository.finishGame(onFinished)
        }
    }

    private fun tick(lastSeconds: Int) = intent {
        reduce { state.copy(lastSeconds = lastSeconds) }
    }

    private fun setGameScreenState(
        seconds: Int,
        hintLimit: Int,
        usedHints: Set<Int>,
        lastSeconds: Int,
        startTime: Long,
        themeImageUrl: String? = null,
        themeImageCustomInfo: ThemeImageCustomInfo? = null
    ) = intent {
        reduce {
            state.copy(
                totalSeconds = seconds,
                totalHintCount = hintLimit,
                usedHints = usedHints,
                lastSeconds = lastSeconds,
                startTime = startTime,
                themeImageUrl = themeImageUrl,
                themeImageCustomInfo = themeImageCustomInfo
            )
        }
    }

    override fun onCleared() {
        timerRepository.stopTimer()
        super.onCleared()
        Timber.d("onCleared: GameViewModel")
    }
}
