package com.nextroom.nextroom.presentation.ui.main

import androidx.lifecycle.SavedStateHandle
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.model.Hint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GameSharedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : NewBaseViewModel() {

    private val _subscribeStatus = MutableStateFlow(
        TimerFragmentArgs.fromSavedStateHandle(savedStateHandle).subscribeStatus
    )
    val subscribeStatus: StateFlow<SubscribeStatus> = _subscribeStatus.asStateFlow()

    private val _currentHint = MutableStateFlow<Hint?>(null)
    val currentHint: StateFlow<Hint?> = _currentHint.asStateFlow()

    private val _openedHintIds = MutableStateFlow<Set<Int>>(emptySet())
    val openedHintIds: StateFlow<Set<Int>> = _openedHintIds.asStateFlow()

    fun setCurrentHint(hint: Hint) {
        _currentHint.value = hint
    }

    fun addOpenedHintId(hintId: Int) {
        _openedHintIds.value += hintId
    }

    fun updateOpenedHintIds(hintIds: Set<Int>) {
        _openedHintIds.value = hintIds
    }

    fun hasOpenedHint(hintId: Int): Boolean {
        return openedHintIds.value.contains(hintId)
    }

    fun getOpenedHintCount() = openedHintIds.value.size
}