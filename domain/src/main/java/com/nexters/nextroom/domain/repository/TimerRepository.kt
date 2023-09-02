package com.nexters.nextroom.domain.repository

import com.nexters.nextroom.domain.model.TimerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    val lastSeconds: Flow<Int>
    val timerState: StateFlow<TimerState>
    fun initTimer(initSeconds: Int)
    fun startTimer()
    fun stopTimer()
}
