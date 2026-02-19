package com.nextroom.nextroom.presentation.ui.tutorial.memo

import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.ui.tutorial.TutorialSharedViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class TutorialMemoViewModel @AssistedInject constructor(
    @Assisted private val tutorialSharedViewModel: TutorialSharedViewModel
) : NewBaseViewModel() {

    private val _uiState = MutableStateFlow(TutorialMemoState())
    val uiState = combine(
        _uiState,
        tutorialSharedViewModel.state
    ) { state, sharedState ->
        state.copy(
            lastSeconds = sharedState.lastSeconds,
            showTooltips = !sharedState.memoTooltipShown,
        )
    }.stateIn(baseViewModelScope, SharingStarted.Lazily, _uiState.value)

    fun pickPen() {
        _uiState.update { it.copy(currentTool = TutorialDrawingTool.Pen) }
    }

    fun pickEraser() {
        _uiState.update { it.copy(currentTool = TutorialDrawingTool.Eraser) }
    }

    fun eraseAll() {
        _uiState.update { it.copy(paths = emptyList(), clearCanvas = true) }
    }

    fun updatePaths(paths: List<PathData>) {
        _uiState.update { it.copy(paths = paths, clearCanvas = false) }
    }

    fun dismissTooltips() {
        tutorialSharedViewModel.markMemoTooltipShown()
    }

    @AssistedFactory
    interface Factory {
        fun create(tutorialSharedViewModel: TutorialSharedViewModel): TutorialMemoViewModel
    }
}
