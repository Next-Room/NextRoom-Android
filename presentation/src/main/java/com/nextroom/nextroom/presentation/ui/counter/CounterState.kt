package com.nextroom.nextroom.presentation.ui.counter

import com.nextroom.nextroom.domain.model.TimerState

data class CounterState(
    val lastSeconds: Int = 3,
    val timerState: TimerState = TimerState.Ready,
)
