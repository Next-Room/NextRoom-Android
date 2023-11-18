package com.nextroom.nextroom.presentation.base

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import timber.log.Timber

abstract class BaseViewModel<STATE : Any, SIDE_EFFECT : Any> :
    ContainerHost<STATE, SIDE_EFFECT>,
    ViewModel() {
    abstract override val container: Container<STATE, SIDE_EFFECT>

    init {
        Timber.tag("MANGBAAM-BaseViewModel()").d("${this::class.simpleName} generated")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.tag("MANGBAAM-BaseViewModel(onCleared)").d("${this::class.simpleName} cleared")
    }
}
