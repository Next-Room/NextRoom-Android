package com.nextroom.nextroom.presentation.ui.background_custom

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation
import com.nextroom.nextroom.presentation.model.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class BackgroundImageCustomDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val themeRepository: ThemeRepository
) : BaseViewModel<BackgroundImageCustomDetailState, BackgroundImageCustomDetailEvent>() {

    private val theme = BackgroundImageCustomDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).theme

    override val container: Container<BackgroundImageCustomDetailState, BackgroundImageCustomDetailEvent> =
        container(BackgroundImageCustomDetailState(themeInfoPresentation = theme))

    private var themeImageCustomInfoUi: ThemeInfoPresentation.ThemeImageCustomInfoUi? = theme.themeImageCustomInfo

    fun onMatrixChanged(left: Float, top: Float, right: Float, bottom: Float) {
        themeImageCustomInfoUi = getThemeImageCustomInfo().copy(
            left = left,
            top = top,
            right = right,
            bottom = bottom
        )
    }

    fun onSaveClicked() = intent {
        viewModelScope.launch {
            try {
                val themeImageCustomInfoUi = themeImageCustomInfoUi ?: return@launch
                themeRepository.upsertTheme(theme.toDomain(themeImageCustomInfoUi))
                postSideEffect(BackgroundImageCustomDetailEvent.SaveSuccess)
            } catch (ex: Exception) {
                postSideEffect(BackgroundImageCustomDetailEvent.UnknownError)
            }
        }
    }

    fun onOpacityChanged(opacity: Int) = intent {
        reduce {
            themeImageCustomInfoUi = getThemeImageCustomInfo().copy(opacity = opacity)
            state.copy(themeInfoPresentation = state.themeInfoPresentation.copy(themeImageCustomInfo = themeImageCustomInfoUi))
        }
    }

    private fun getThemeImageCustomInfo(): ThemeInfoPresentation.ThemeImageCustomInfoUi {
        return themeImageCustomInfoUi ?: ThemeInfoPresentation.ThemeImageCustomInfoUi()
    }
}
