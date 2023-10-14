package com.nextroom.nextroom.presentation.ui.memo

import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.repository.TimerRepository
import com.nextroom.nextroom.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
) : BaseViewModel<MemoState, MemoEvent>() {
    override val container: Container<MemoState, MemoEvent> = container(MemoState())

    init {
        viewModelScope.launch {
            timerRepository.lastSeconds.collect(::tick)
        }
    }

    fun pickPen() = intent {
        reduce { state.copy(currentTool = DrawingTool.Pen) }
    }

    fun pickEraser() = intent {
        reduce { state.copy(currentTool = DrawingTool.Eraser) }
    }

    fun toggleTool() = intent {
        when (state.currentTool) {
            DrawingTool.Eraser -> pickPen()
            DrawingTool.Pen -> pickEraser()
        }
    }

    fun eraseAll() = intent {
        postSideEffect(MemoEvent.EraseAll)
    }

    private fun tick(lastSeconds: Int) = intent {
        Timber.d("tick: $lastSeconds")
        reduce { state.copy(lastSeconds = lastSeconds) }
    }
}
