package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.domain.model.TimerState
import com.nextroom.nextroom.domain.repository.TimerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class TimerRepositoryImpl @Inject constructor(
    private val defaultDispatcher: CoroutineDispatcher,
) : TimerRepository {

    private var timerJob: Job? = null

    private val _timerState: MutableStateFlow<TimerState> =
        MutableStateFlow(TimerState.UnInitialized)
    override val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var _lastMillis = MutableStateFlow(0L)
    override val lastSeconds: Flow<Int> = _lastMillis.map { (it / 1000).coerceAtLeast(0).toInt() }

    override fun startTimerUntil(endTimeMillis: Long) {
        Timber.d("timerUntil $endTimeMillis")
        timerJob = CoroutineScope(defaultDispatcher).launch {
            setTimerState(TimerState.Running)
            _lastMillis.value = endTimeMillis - System.currentTimeMillis()
            do {
                delay(300) // 숫자가 클수록 간헐적으로 시간 지연이 발생할 수 있으나 리소스 낭비는 줄어듦
                _lastMillis.value = endTimeMillis - System.currentTimeMillis()
            } while (isActive && _lastMillis.value > 0)
            stopTimer()
        }
    }

    override fun stopTimer() {
        timerJob?.cancel()
        setTimerState(TimerState.Finished)
    }

    private fun setTimerState(newState: TimerState) {
        _timerState.value = newState
    }
}
