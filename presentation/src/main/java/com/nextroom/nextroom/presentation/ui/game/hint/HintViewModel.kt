package com.nextroom.nextroom.presentation.ui.game.hint

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.navGraphViewModels
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.ui.game.shared.GameSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed interface HintEvent

data class HintState(
    val hintText: String = "",
    val canShowHint: Boolean = false,
    val subscribeStatus: Boolean = false
)

@HiltViewModel
class HintViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val gameSharedViewModel: GameSharedViewModel // GameSharedViewModel 주입
) : BaseViewModel<HintState, HintEvent>() {

    override val container = container<HintState, HintEvent>(
        initialState = HintState()
    )

    init {
        // GameSharedViewModel에서 힌트 정보와 구독 상태를 가져와 HintState에 반영
        onCollect(gameSharedViewModel.container.stateFlow) { sharedState ->
            intent {
                reduce { state.copy(
                    hintText = sharedState.hintInfo,
                    subscribeStatus = sharedState.subscribeStatus
                ) }
            }
        }
    }

    fun fetchHint() {
        // Hint 정보를 가져오는 로직
        // Hint 정보를 가져온 후 GameSharedViewModel의 hintInfo 업데이트
        val fetchedHint = "This is the fetched hint."
        gameSharedViewModel.updateHintInfo(fetchedHint)
    }

    fun toggleSubscriptionStatus() {
        // 구독 상태 변경 로직
        val newStatus = !state.value.subscribeStatus
        gameSharedViewModel.updateSubscribeStatus(newStatus)
    }
}
