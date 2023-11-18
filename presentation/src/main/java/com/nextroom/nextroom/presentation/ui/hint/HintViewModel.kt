package com.nextroom.nextroom.presentation.ui.hint

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.repository.TimerRepository
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.model.Hint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HintViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val timerRepository: TimerRepository,
) : BaseViewModel<HintState, HintEvent>() {
    override val container: Container<HintState, HintEvent> = container(
        HintState(
            hint = savedStateHandle.get<Hint>("hint") ?: Hint(),
        ),
    )

    init {
        viewModelScope.launch {
            timerRepository.lastSeconds.collect(::tick)
        }
    }

    fun openAnswer() = intent {
        reduce { state.copy(hint = state.hint.copy(answerOpened = true)) }
    }

    private fun tick(lastSeconds: Int) = intent {
        reduce { state.copy(lastSeconds = lastSeconds) }
    }
}
