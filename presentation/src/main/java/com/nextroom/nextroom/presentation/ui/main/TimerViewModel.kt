package com.nextroom.nextroom.presentation.ui.main

import com.nextroom.nextroom.domain.model.GameState
import com.nextroom.nextroom.domain.model.ThemeImageCustomInfo
import com.nextroom.nextroom.domain.model.ThemeInfo
import com.nextroom.nextroom.domain.model.TimerState
import com.nextroom.nextroom.domain.repository.GameStateRepository
import com.nextroom.nextroom.domain.repository.HintRepository
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.domain.repository.TimerRepository
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.model.Hint
import com.nextroom.nextroom.presentation.model.InputState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber

class TimerViewModel @AssistedInject constructor(
    private val themeRepository: ThemeRepository,
    private val timerRepository: TimerRepository,
    private val gameStateRepository: GameStateRepository,
    private val hintRepository: HintRepository,
    @Assisted private val gameSharedViewModel: GameSharedViewModel
) : BaseViewModel<TimerScreenState, TimerEvent>() {

    override val container: Container<TimerScreenState, TimerEvent> = container(TimerScreenState())

    val uiState = combine(
        container.stateFlow,
        gameSharedViewModel.state
    ) { state, gameSharedState ->
        state.copy(openedHintCount = gameSharedState.openedHintIds.size)
    }.stateIn(
        baseViewModelScope,
        SharingStarted.Lazily,
        container.stateFlow.value
    )

    init {
        baseViewModelScope.launch {
            timerRepository.lastSeconds.collect(::tick)
        }
        baseViewModelScope.launch {
            val latestGame = gameStateRepository.getGameState()
            fun isNewGame() = latestGame == null
            if (isNewGame()) {
                showGameBackgroundImage(loadLatestTheme())
                showGameStartConfirmDialog()
            } else if (latestGame != null) {
                resumeGame(latestGame, modifiedTimeInMinute = null)
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
        postSideEffect(TimerEvent.NewTimer)
    }

    fun onGameStartClicked() {
        startOrResumeGame()
    }

    private suspend fun loadLatestTheme(): ThemeInfo {
        return themeRepository.getLatestTheme().first()
    }

    fun restartGameWithModifiedTime(modifiedTimeInMinute: Int) {
        timerRepository.stopTimer()
        startOrResumeGame(modifiedTimeInMinute)
    }

    private fun startOrResumeGame(modifiedTimeInMinute: Int? = null) {
        baseViewModelScope.launch {
            gameStateRepository.getGameState()?.let { gameState -> // 비정상 종료된 게임이 존재하거나 수정된 시간으로 게임을 재개하는 경우
                resumeGame(gameState, modifiedTimeInMinute)
            } ?: run { // 새로운 게임을 시작하는 경우
                with(themeRepository.getLatestTheme().first()) {
                    val startTime = System.currentTimeMillis()
                    gameStateRepository.saveGameState(
                        timeLimitInMinute = timeLimitInMinute,
                        hintLimit = hintLimit,
                        usedHints = emptySet(),
                        startTime = System.currentTimeMillis(),
                        useTimerUrl = useTimerUrl,
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
                        themeImageCustomInfo = themeImageCustomInfo,
                        themeImageEnabled = useTimerUrl,
                    )
                    startGame(startTime + timeLimitInMinute * 60 * 1000)
                }
            }
        }

        // 게임 시작 통계 집계 시작
//                statsRepository.recordGameStartStats(GameStats(it.id, DateTimeUtil().currentTime() ?: ""))
    }

    private suspend fun resumeGame(gameState: GameState, modifiedTimeInMinute: Int?) {
        if (modifiedTimeInMinute == null) {
            with(gameState) {
                setGameScreenState(
                    seconds = timeLimitInMinute * 60,
                    hintLimit = hintLimit,
                    usedHints = usedHints,
                    lastSeconds = lastSeconds,
                    startTime = startTime,
                    themeImageUrl = themeImageUrl,
                    themeImageCustomInfo = themeImageCustomInfo,
                    themeImageEnabled = useTimerUrl,
                )
                startGame(startTime + timeLimitInMinute * 60 * 1000)
            }
        } else {
            val startTime = System.currentTimeMillis()
            with(gameState) {
                gameStateRepository.saveGameState(
                    timeLimitInMinute = modifiedTimeInMinute,
                    hintLimit = hintLimit,
                    usedHints = usedHints,
                    startTime = startTime,
                    useTimerUrl = useTimerUrl,
                    themeImageUrl = themeImageUrl,
                    themeImageCustomInfo = themeImageCustomInfo
                )
                setGameScreenState(
                    seconds = modifiedTimeInMinute * 60,
                    hintLimit = hintLimit,
                    usedHints = usedHints,
                    lastSeconds = modifiedTimeInMinute * 60,
                    startTime = startTime,
                    themeImageUrl = themeImageUrl,
                    themeImageCustomInfo = themeImageCustomInfo,
                    themeImageEnabled = useTimerUrl,
                )
                startGame(startTime + modifiedTimeInMinute * 60 * 1000)
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
        postSideEffect(TimerEvent.ClearHintCode)
    }

    private fun validateHintCode() = intent {
        suspend fun openHint(hint: com.nextroom.nextroom.domain.model.Hint) {
            reduce {
                state.copy(inputState = InputState.Ok, openedHintCount = gameSharedViewModel.getOpenedHintCount())
            }
            postSideEffect(
                TimerEvent.OnOpenHint(
                    Hint(
                        id = hint.id,
                        progress = hint.progress,
                        hint = hint.description,
                        answer = hint.answer,
                        answerOpened = gameSharedViewModel.state.value.openedHintIds.contains(hint.id),
                        hintImageUrlList = hint.hintImageUrlList.toList(),
                        answerImageUrlList = hint.answerImageUrlList.toList()
                    )
                ),
            )
        }

        if (timerRepository.timerState.value is TimerState.Finished) {
            postSideEffect(TimerEvent.TimerFinish)
            delay(500)
            clearHintCode()
            return@intent
        }

        hintRepository.getHint(state.currentInput)?.let { hint ->
            openHint(hint)
        } ?: run {
            reduce { state.copy(inputState = InputState.Error(R.string.game_wrong_hint_code)) }
            delay(500)
            clearHintCode()
        }
    }

    private fun startGame(endTimeMillis: Long) = intent {
        timerRepository.startTimerUntil(endTimeMillis)
    }

    fun finishGame(onFinished: () -> Unit = {}) = intent {
        baseViewModelScope.launch(Dispatchers.Main) {
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
        themeImageCustomInfo: ThemeImageCustomInfo? = null,
        themeImageEnabled: Boolean,
    ) = intent {
        gameSharedViewModel.updateOpenedHintIds(usedHints)
        gameSharedViewModel.setTotalHintCount(hintLimit)
        reduce {
            state.copy(
                totalSeconds = seconds,
                totalHintCount = hintLimit,
                lastSeconds = lastSeconds,
                openedHintCount = usedHints.size,
                startTime = startTime,
                themeImageUrl = themeImageUrl,
                themeImageCustomInfo = themeImageCustomInfo,
                themeImageEnabled = themeImageEnabled
            )
        }
    }

    fun checkGameFinished(): Boolean {
        return timerRepository.timerState.value is TimerState.Finished
    }

    override fun onCleared() {
        timerRepository.stopTimer()
        super.onCleared()
        Timber.d("onCleared: GameViewModel")
    }

    @AssistedFactory
    interface Factory {
        fun create(gameSharedViewModel: GameSharedViewModel): TimerViewModel
    }
}
