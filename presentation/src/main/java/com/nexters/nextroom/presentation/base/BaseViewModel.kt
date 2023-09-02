package com.nexters.nextroom.presentation.base

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import timber.log.Timber

abstract class BaseViewModel<STATE : Any, SIDE_EFFECT : Any> :
    ContainerHost<STATE, SIDE_EFFECT>,
    ViewModel() {
    abstract override val container: Container<STATE, SIDE_EFFECT>

    init {
        Timber.d("${this::class.simpleName} generated")
    }
}
