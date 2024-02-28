package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.TimerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    val lastSeconds: Flow<Int>
    val timerState: StateFlow<TimerState>

    /**
     * 시스템 시간을 기반으로 하는 타이머
     *
     * @param endTimeMillis 종료될 시각의 밀리초 (시작 시각 밀리초 + 플레이 시간(분) * 60 * 1000)
     */
    fun startTimerUntil(endTimeMillis: Long)

    fun stopTimer()
}
