package com.nextroom.nextroom.presentation.ui.game.shared

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.navGraphViewModels
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.ui.game.hint.HintState
import com.nextroom.nextroom.presentation.ui.game.timer.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed interface GameSharedEvent

data class GameSharedState(
    val hintInfo: String = "",
    val subscribeStatus: Boolean = false
)

@HiltViewModel
class GameSharedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<GameSharedState, GameSharedEvent>() {

    override val container = container<GameSharedState, GameSharedEvent>(
        initialState = GameSharedState()
    )

    fun updateHintInfo(info: String) {
        intent {
            reduce { state.copy(hintInfo = info) }
        }
    }

    fun updateSubscribeStatus(status: Boolean) {
        intent {
            reduce { state.copy(subscribeStatus = status) }
        }
    }

    // TODO: TimerViewModel 및 HintViewModel에서 GameSharedViewModel을 참조하여
    // State를 업데이트하거나 가져오는 로직 구현
}