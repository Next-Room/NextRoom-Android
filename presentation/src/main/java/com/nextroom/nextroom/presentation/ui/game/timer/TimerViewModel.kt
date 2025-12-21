package com.nextroom.nextroom.presentation.ui.game.timer

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.navGraphViewModels
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.ui.game.shared.GameSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed interface TimerEvent

data class TimerState(
    val elapsedTime: Long = 0L,
    val isRunning: Boolean = false
)

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val gameSharedViewModel: GameSharedViewModel // GameSharedViewModel 주입
) : BaseViewModel<TimerState, TimerEvent>() {

    override val container = container<TimerState, TimerEvent>(
        initialState = TimerState()
    )

    init {
        // GameSharedViewModel에서 구독 상태를 가져와 TimerState에 반영하거나, TimerState에 반영된 구독 상태를 GameSharedViewModel에 전달
        // 예시: onCollect(gameSharedViewModel.container.stateFlow) { sharedState -> 
        //    intent { reduce { state.copy(someFieldRelatedToSubscription = sharedState.subscribeStatus) } }
        // }
    }

    fun startTimer() {
        // Timer 로직...
        intent {
            reduce { state.copy(isRunning = true) }
        }
    }

    fun pauseTimer() {
        // Timer 로직...
        intent {
            reduce { state.copy(isRunning = false) }
        }
    }

    fun resetTimer() {
        // Timer 로직...
        intent {
            reduce { state.copy(elapsedTime = 0L, isRunning = false) }
        }
    }

    // TODO: HintFragment로 이동 시 힌트 정보 업데이트 로직 추가
    // 예시: fun navigateToHintFragment() {
    //     gameSharedViewModel.updateHintInfo("Fetched Hint Info")
    // }
}
