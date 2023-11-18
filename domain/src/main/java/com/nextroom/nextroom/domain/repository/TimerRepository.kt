package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.TimerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    val lastSeconds: Flow<Int>
    val timerState: StateFlow<TimerState>
    fun initTimer(initSeconds: Int)
    fun startTimer()
    fun stopTimer()

    /**
     * 시간 보정
     *
     * @param milliseconds 보정할 시간(밀리초). 양수이면 남은 시간에 더해지고, 음수이면 남은 시간에서 차감됨.
     */
    fun correctTime(milliseconds: Long)
}
