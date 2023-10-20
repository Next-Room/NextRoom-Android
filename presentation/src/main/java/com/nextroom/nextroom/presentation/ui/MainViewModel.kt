package com.nextroom.nextroom.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.repository.GameStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    gameStateRepository: GameStateRepository,
) : ViewModel() {

    private val _event = MutableSharedFlow<MainEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            gameStateRepository.getGameState()?.let { gameState ->
                if (gameState.playing) {
                    event(MainEvent.GoToGameScreen(gameState))
                }
            }
        }
    }

    private fun event(event: MainEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
