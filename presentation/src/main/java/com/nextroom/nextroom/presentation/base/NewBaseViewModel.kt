package com.nextroom.nextroom.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.presentation.BuildConfig
import com.nextroom.nextroom.presentation.util.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.plus
import kotlin.coroutines.cancellation.CancellationException

// 기존에 사용하던 BaseViewModel을 다 걷어내면
// 이 파일을 BaseViewModel로 이름을 변경해 사용한다.
abstract class NewBaseViewModel : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    protected val baseViewModelScope = viewModelScope + exceptionHandler

    private val _errorFlow = MutableSharedFlow<Throwable>(extraBufferCapacity = 1)
    val errorFlow = _errorFlow.asSharedFlow()

    fun handleError(throwable: Throwable) {
        when (throwable) {
            is CancellationException -> Unit
            else -> {
                if (!BuildConfig.DEBUG) {
                    Logger.e("${this::class.simpleName} generated\n${throwable.message}")
                }

                _errorFlow.tryEmit(throwable)
            }
        }
    }
}