package com.nextroom.nextroom.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.domain.repository.FirebaseRemoteConfigRepository
import com.nextroom.nextroom.domain.repository.FirebaseRemoteConfigRepository.Companion.REMOTE_KEY_APP_MIN_VERSION
import com.nextroom.nextroom.domain.repository.GameStateRepository
import com.nextroom.nextroom.domain.usecase.CompareVersion
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
    private val firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository,
    private val compareVersion: CompareVersion,
) : ViewModel() {

    private val _event = MutableSharedFlow<MainEvent>()
    val event = _event.asSharedFlow()

    val loginState: StateFlow<Boolean> = adminRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false,
    )

    val authEvent = adminRepository.authEvent

    init {
        viewModelScope.launch {
            try {
                adminRepository.getUserSubscribe().getOrThrow.status
            } catch (e: Exception) {
                SubscribeStatus.Default
            }.also { subscribeStatus ->
                gameStateRepository.getGameState()?.let { gameState ->
                    event(MainEvent.GoToGameScreen(gameState, subscribeStatus))
                }
            }
        }
        viewModelScope.launch {
            firebaseRemoteConfigRepository
                .getFirebaseRemoteConfigValue(REMOTE_KEY_APP_MIN_VERSION)
                .collect { minVersion ->
                    compareVersion(minVersion = minVersion)
                }
        }
    }

    fun logout() {
        event(MainEvent.GoToLoginScreen)
    }

    private fun event(event: MainEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    private fun compareVersion(minVersion: String) {
        compareVersion
            .invoke(minVersion)
            .also { updateStatus ->
                if (updateStatus == CompareVersion.UpdateStatus.NEED_FORCE_UPDATE) {
                    event(MainEvent.ShowForceUpdateDialog)
                }
            }
    }
}
