package com.nextroom.nextroom.presentation.ui.tutorial.hint

import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.ui.tutorial.TutorialSharedViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TutorialHintViewModel @AssistedInject constructor(
    @Assisted private val tutorialSharedViewModel: TutorialSharedViewModel
) : NewBaseViewModel() {

    private val _uiState = MutableStateFlow(TutorialHintState())
    val uiState = combine(
        _uiState,
        tutorialSharedViewModel.state
    ) { state, sharedState ->
        state.copy(
            hint = sharedState.currentHint ?: state.hint,
            isHintOpened = (sharedState.currentHint?.id ?: 0) in sharedState.openedHintIds,
            isAnswerOpened = (sharedState.currentHint?.id ?: 0) in sharedState.openedAnswerIds,
            totalHintCount = sharedState.totalHintCount,
            lastSeconds = sharedState.lastSeconds
        )
    }.stateIn(baseViewModelScope, SharingStarted.Lazily, _uiState.value)

    fun openHint() {
        tutorialSharedViewModel.state.value.currentHint?.let { hint ->
            tutorialSharedViewModel.addOpenedHintId(hint.id)
        }
    }

    fun openAnswer() {
        tutorialSharedViewModel.state.value.currentHint?.let { hint ->
            tutorialSharedViewModel.addOpenedAnswerId(hint.id)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(tutorialSharedViewModel: TutorialSharedViewModel): TutorialHintViewModel
    }
}
