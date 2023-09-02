package com.nexters.nextroom.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.nextroom.domain.repository.AdminRepository
import com.nexters.nextroom.domain.repository.GameStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    adminRepository: AdminRepository,
    gameStateRepository: GameStateRepository,
) : ViewModel() {

    private val _event = MutableSharedFlow<MainEvent>()
    val event = _event.asSharedFlow()

    val loginState: StateFlow<Boolean> = adminRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false,
    )

    init {
        viewModelScope.launch {
            gameStateRepository.getGameState()?.let { gameState ->
                if (gameState.playing) {
                    event(MainEvent.GoToGameScreen(gameState))
                }
            }
        }
    }

    fun logout() {
        event(MainEvent.GoToAdminCode)
    }

    private fun event(event: MainEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}
