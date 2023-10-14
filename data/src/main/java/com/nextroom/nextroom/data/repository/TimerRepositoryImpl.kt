package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.domain.model.TimerState
import com.nextroom.nextroom.domain.repository.TimerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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

    private var lastTimeMillis: Long = 0

    private var _lastMillis = MutableStateFlow(0L)
    override val lastSeconds: Flow<Int> = _lastMillis.map { (it / 1000).toInt() }

    override fun initTimer(initSeconds: Int) {
        Timber.d("initTimer")
        _lastMillis.value = initSeconds * 1000L
        setTimerState(TimerState.Ready)
    }

    override fun startTimer() {
        Timber.d("startTimer")
        timerJob = CoroutineScope(defaultDispatcher).launch {
            setTimerState(TimerState.Running)
            lastTimeMillis = System.currentTimeMillis()
            while (isActive && _lastMillis.value > 0) {
                val delayMillis = System.currentTimeMillis() - lastTimeMillis
                if (delayMillis >= TICK) {
                    _lastMillis.value = (_lastMillis.value - TICK).coerceAtLeast(0) // 시간 감소
                    lastTimeMillis = System.currentTimeMillis()
                }
            }
            setTimerState(TimerState.Finished)
        }
    }

    override fun stopTimer() {
        timerJob?.cancel()
        setTimerState(TimerState.Finished)
    }

    private fun setTimerState(newState: TimerState) {
        _timerState.value = newState
    }

    companion object {
        const val TICK = 1000L
    }
}
