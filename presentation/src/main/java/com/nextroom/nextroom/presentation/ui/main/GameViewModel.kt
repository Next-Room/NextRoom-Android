package com.nextroom.nextroom.presentation.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.model.Hint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GameSharedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // subscribeStatus는 상위 navigation에서 전달받음
    private val _subscribeStatus = MutableStateFlow(
        TimerFragmentArgs.fromSavedStateHandle(savedStateHandle).subscribeStatus
    )
    val subscribeStatus: StateFlow<SubscribeStatus> = _subscribeStatus.asStateFlow()

    // hint는 TimerFragment에서 설정
    private val _currentHint = MutableStateFlow<Hint?>(null)
    val currentHint: StateFlow<Hint?> = _currentHint.asStateFlow()

    /**
     * Called by TimerFragment when user opens a hint
     */
    fun setCurrentHint(hint: Hint) {
        _currentHint.value = hint
    }

    /**
     * Called when HintFragment updates hint state (e.g., answer opened)
     */
    fun updateCurrentHint(hint: Hint) {
        _currentHint.value = hint
    }

    /**
     * Clear hint when returning to timer screen
     */
    fun clearCurrentHint() {
        _currentHint.value = null
    }
}