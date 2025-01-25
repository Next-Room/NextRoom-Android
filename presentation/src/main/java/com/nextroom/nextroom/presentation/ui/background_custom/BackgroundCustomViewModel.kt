package com.nextroom.nextroom.presentation.ui.background_custom

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation
import com.nextroom.nextroom.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class BackgroundCustomViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val themeRepository: ThemeRepository
) : BaseViewModel<BackgroundCustomState, BackgroundCustomEvent>() {

    override val container: Container<BackgroundCustomState, BackgroundCustomEvent> =
        container(BackgroundCustomState(BackgroundCustomFragmentArgs.fromSavedStateHandle(savedStateHandle).themes.toList()))

    fun toggleImage(theme: ThemeInfoPresentation) = intent {
        if (theme.themeImageUrl.isNullOrEmpty()) {
            postSideEffect(BackgroundCustomEvent.ToggleImageError(R.string.text_background_setting_error))
            return@intent
        }

        when (getUserSubscribeStatus()) {
            SubscribeStatus.Subscribed -> updateToggle(theme)
            SubscribeStatus.Default,
            SubscribeStatus.SUBSCRIPTION_EXPIRATION -> {
                try {
                    val themes = container.stateFlow.value.themes
                    val activeThemeImageCount = themes.count { it.useTimerUrl }
                    if (activeThemeImageCount == 0) {
                        updateToggle(theme)
                    } else {
                        val activeTheme = themes.first { it.useTimerUrl }
                        if (activeTheme.id == theme.id) {
                            updateToggle(theme)
                        } else {
                            postSideEffect(BackgroundCustomEvent.ToggleNotAllowed)
                        }
                    }
                } catch (ex: Exception) {
                    postSideEffect(BackgroundCustomEvent.UnknownError)
                }
            }
        }
    }

    fun onThemeImageClicked(theme: ThemeInfoPresentation) = intent {
        viewModelScope.launch {
            try {
                val updatedAt = themeRepository.getUpdatedInfo(theme.id)
                themeRepository.getThemeById(theme.id)
                    .toPresentation(updatedAt)
                    .let {
                        postSideEffect(BackgroundCustomEvent.ThemeImageClicked(it))
                    }
            } catch (ex: Exception) {
                postSideEffect(BackgroundCustomEvent.UnknownError)
            }
        }
    }

    private fun updateToggle(theme: ThemeInfoPresentation) = intent {
        viewModelScope.launch {
            container.stateFlow.value.themes
                .map {
                    if (it.id == theme.id) it.copy(useTimerUrl = !it.useTimerUrl)
                    else it
                }.let { themes ->
                    val activeThemeIdList = themes.filter { it.useTimerUrl }.map { it.id }
                    val deActiveThemeIdList = themes.filter { !it.useTimerUrl }.map { it.id }
                    themeRepository.activateThemeBackgroundImage(activeThemeIdList, deActiveThemeIdList)
                        .onSuccess {
                            reduce { state.copy(themes = themes) }
                        }.onFailure {
                            handleError(it)
                        }
                }
        }
    }

    private fun handleError(error: Result.Failure) = intent {
        when (error) {
            is Result.Failure.NetworkError -> postSideEffect(BackgroundCustomEvent.NetworkError)
            is Result.Failure.HttpError -> postSideEffect(BackgroundCustomEvent.ClientError(error.message))
            else -> postSideEffect(BackgroundCustomEvent.UnknownError)
        }
    }

    private fun getUserSubscribeStatus(): SubscribeStatus {
        return BackgroundCustomFragmentArgs.fromSavedStateHandle(savedStateHandle).subscribeStatus
    }
}
