package com.nextroom.nextroom.presentation.ui.background_custom

import androidx.lifecycle.SavedStateHandle
import com.nextroom.nextroom.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class BackgroundCustomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<BackgroundCustomState, BackgroundCustomEvent>() {

    override val container: Container<BackgroundCustomState, BackgroundCustomEvent> =
        container(BackgroundCustomState(BackgroundCustomFragmentArgs.fromSavedStateHandle(savedStateHandle).themes.toList()))


}
