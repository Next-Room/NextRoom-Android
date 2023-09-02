package com.nexters.nextroom.domain.model

import java.io.IOException
import java.lang.Exception

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>

    sealed interface Failure : Result<Nothing> {
        sealed interface HttpError : Failure {
            val code: Int
            val message: String

            data class NotFound(override val message: String) : HttpError {
                override val code = 404
            }
            data class ServerError(override val message: String) : HttpError {
                override val code = 500
            }
        }

        sealed interface LocalError : Failure {
            data class IOError(val throwable: Throwable) : LocalError

            companion object {
                inline fun <R> runCatching(block: () -> R): Result<R> {
                    return try {
                        Success(block())
                    } catch (e: IOException) {
                        IOError(e)
                    } catch (e: Exception) {
                        UnknownError(e)
                    }
                }
            }
        }
        data class NetworkError(val throwable: Throwable) : Failure
        data class UnknownError(val throwable: Throwable) : Failure
    }

    val isSuccessful
        get() = this is Success<T>
    val isFailure
        get() = this is Failure
    val getOrThrow: T
        get() = (this as? Success)?.data ?: error("Check if result is Success")
    val getOrNull: T?
        get() = (this as? Success<T>)?.data
    val failureOrThrow: Failure
        get() = this as? Failure ?: error("Check if result is Failure")
    val failureOrNull: Failure?
        get() = this as? Failure
}

inline fun <T> Result<T>.onSuccess(action: (data: T) -> Unit): Result<T> {
    if (isSuccessful) action(getOrThrow)
    return this
}

suspend fun <T> Result<T>.suspendOnSuccess(action: suspend (data: T) -> Unit): Result<T> {
    if (isSuccessful) action(getOrThrow)
    return this
}

inline fun <T> Result<T>.onFailure(action: (error: Result.Failure) -> Unit): Result<T> {
    if (isFailure) action(failureOrThrow)
    return this
}

suspend fun <T> Result<T>.onSuspendFailure(action: suspend (error: Result.Failure) -> Unit): Result<T> {
    if (isFailure) action(failureOrThrow)
    return this
}

fun <T, R> Result<T>.mapOnSuccess(map: (T) -> R): Result<R> {
    return if (isSuccessful) Result.Success(map(getOrThrow)) else failureOrThrow
}
