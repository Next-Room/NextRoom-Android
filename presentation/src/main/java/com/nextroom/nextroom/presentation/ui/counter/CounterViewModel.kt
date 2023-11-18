package com.nextroom.nextroom.presentation.ui.counter

import com.nextroom.nextroom.domain.model.TimerState
import com.nextroom.nextroom.presentation.BuildConfig
import com.nextroom.nextroom.presentation.base.BaseViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class CounterViewModel : BaseViewModel<CounterState, Nothing>() {
    override val container: Container<CounterState, Nothing> = container(CounterState())

    private var leaveTime: Long? = null

    init {
        startCounter(if (BuildConfig.DEBUG) 3 else 10)
    }

    fun onLeave() {
        leaveTime = System.currentTimeMillis()
    }

    fun getOverflowTimeMillis(): Long = leaveTime?.let { System.currentTimeMillis() - it } ?: 0

    private fun startCounter(startSeconds: Int = 10) = intent {
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
