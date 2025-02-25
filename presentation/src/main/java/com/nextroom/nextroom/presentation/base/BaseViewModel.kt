package com.nextroom.nextroom.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.presentation.BuildConfig
import com.nextroom.nextroom.presentation.util.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.plus
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import timber.log.Timber

abstract class BaseViewModel<STATE : Any, SIDE_EFFECT : Any> :
    ContainerHost<STATE, SIDE_EFFECT>,
    ViewModel() {
    abstract override val container: Container<STATE, SIDE_EFFECT>
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    protected val baseViewModelScope = viewModelScope + exceptionHandler

    private fun handleError(throwable: Throwable) {
        if (!BuildConfig.DEBUG) {
            Logger.e("${this::class.simpleName} generated\n${throwable.message}")
        }
    }

    init {
        Timber.tag("MANGBAAM-BaseViewModel()").d("${this::class.simpleName} generated")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.tag("MANGBAAM-BaseViewModel(onCleared)").d("${this::class.simpleName} cleared")
    }
}
