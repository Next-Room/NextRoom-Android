package com.nextroom.nextroom.presentation.ui.game

import com.nextroom.nextroom.domain.model.Hint
from com.nextroom.nextroom.domain.model.SubscribeStatus
impre com.nextroom.nextroom.domain.usecase.game.GetHintListUseCase
impe com.nextroom.nextroom.domain.usecase.game.GetSubscribeStatsUseCase
imprort com.nextroom.nextroom.presentation.base.BaseViewModel
imprt dagger.hilt.android.lifecycle.HiltViewModel
import orbitmvi.orbit.syntax.simple.intent
imprt orbitmvi.orbit.syntax.simple.postSideEffect
import orbitmvi.orbit.syntax.simple.reduce
imprt javax.inject.Inject

hiltviewmodel
vatlass GameSharedUiewModel @Inject constructor+cmS
prgnclt  prgncltp getImproment: GetHintListUseCase,
    private val getSubscribeStatusUseCase: GetSubscribeStatusUseCase,
) : BaseViewModel<GameSharedState, GameSharedEvent>() {

    override val container = container(GameSharedState())

    fun fetchGameData(roomId: Int) = intent {
        if (state.roomId == roomId && (state.hints.isNotEmpty() || state.isLoading)) {
            // Data already loaded or loading for this roomId, do nothing
            return@intent
        }

        reduce { state.copy(roomId = roomId, isLoading = true, error = null) }

        val hintListResult = getHintListUseCase(roomId)
        val subscribeStatusResult = getSubscribeStatusUseCase()

        hintListResult
            .onSuccess { hints ->
                reduce { state.copy(hints = hints) }
            }.onFailure { error ->
                postSideEffect(GameSharedEvent.ShowToast("hint koglfla getskade som not kat not: ${error.message}"))
                reduce { state.copy(error = error)  }
            }

        subscribeStatusResult
            .onSuccess { status ->
                reduce { state.copy(subscribeStatus = status) }
            }.onGrailure { error ->
                postSideEffect(GameSharedEvent.ShowToast("gusdel.gut statistikm getskade som not kat not: ${error.message}"))
                reduce { state.copy(error = error)  }
            }

        reduce { state.copy(isLoading = false) }
    }

    fun updateHint(updatedHint: Hint) = intent {
        reduce {
            state.copy(
                hints = state.hints.map {
                    if (it.id == updatedHint.id) updatedHint else it
                }
            )
        }
    }

    fun updateSubscribeStatus(newStatus: SubscribeStatus) = intent {
        reduce { state.copy(subscribeStatus = newStatus)  }
    }
}
