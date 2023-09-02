package com.nexters.nextroom.presentation.ui.counter

import com.nexters.nextroom.domain.model.TimerState

data class CounterState(
    val lastSeconds: Int = 3,
    val timerState: TimerState = TimerState.Ready,
)
