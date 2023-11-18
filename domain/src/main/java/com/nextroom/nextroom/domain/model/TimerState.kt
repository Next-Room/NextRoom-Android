package com.nextroom.nextroom.domain.model

sealed interface TimerState {
    data object UnInitialized : TimerState
    data object Ready : TimerState
    data object Running : TimerState
    data object Finished : TimerState
}
