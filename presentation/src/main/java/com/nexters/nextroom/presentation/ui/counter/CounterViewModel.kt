package com.nexters.nextroom.presentation.ui.counter

import com.nexters.nextroom.domain.model.TimerState
import com.nexters.nextroom.presentation.base.BaseViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class CounterViewModel : BaseViewModel<CounterState, Nothing>() {
    override val container: Container<CounterState, Nothing> = container(CounterState())

    fun startCounter(startSeconds: Int = 10) = intent {
        reduce { state.copy(timerState = TimerState.Running, lastSeconds = startSeconds) }
        while (state.timerState is TimerState.Running && state.lastSeconds > 0) {
            delay(1000)
            reduce { state.copy(lastSeconds = state.lastSeconds - 1) }
        }
        if (state.lastSeconds == 0) {
            reduce { state.copy(timerState = TimerState.Finished) }
        }
    }
}
