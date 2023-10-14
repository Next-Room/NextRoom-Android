package com.nextroom.nextroom.domain.model

sealed interface TimerState {
    object UnInitialized : TimerState
    object Ready : TimerState
    object Running : TimerState
    object Finished : TimerState
}
