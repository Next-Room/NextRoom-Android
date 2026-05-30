package com.nextroom.nextroom.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.presentation.BuildConfig
import com.nextroom.nextroom.presentation.util.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.plus
import kotlin.coroutines.cancellation.CancellationException

// 기존에 사용하던 BaseViewModel을 다 걷어내면
// 이 파일을 BaseViewModel로 이름을 변경해 사용한다.
abstract class NewBaseViewModel : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    protected val baseViewModelScope = viewModelScope + exceptionHandler

    private val _errorChannel = Channel<ErrorEvent>(Channel.BUFFERED)
    val errorFlow = _errorChannel.receiveAsFlow()

    fun handleError(throwable: Throwable, action: ErrorAction = ErrorAction.STAY) {
        when (throwable) {
            is CancellationException -> Unit
            else -> {
                if (!BuildConfig.DEBUG) {
                    Logger.e("${this::class.simpleName} generated\n${throwable.message}")
                }

                _errorChannel.trySend(ErrorEvent(throwable, action))
            }
        }
    }

    enum class ErrorAction { STAY, POP_BACK_STACK }

    data class ErrorEvent(val throwable: Throwable, val action: ErrorAction)
}
