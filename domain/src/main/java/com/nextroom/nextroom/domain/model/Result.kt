package com.nextroom.nextroom.domain.model

import java.io.IOException

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>

    sealed interface Failure : Result<Nothing> {
        sealed interface HttpError : Failure {
            val code: Int
            val message: String

            /**
             * ## 잘못된 요청
             *
             * 정의해놓은 에러 케이스에 걸리지 않으면 가장 마지막에 걸리는 에러
             *
             * @property message
             * - 잘못된 요청입니다.
             * - 관리자 코드 또는 패스워드가 일치하지 않습니다.
             * - 업체가 이미 존재합니다.
             */
            data class BadRequest(override val message: String) : HttpError {
                override val code = 400
            }

            /**
             * ## 접근 권한 에러
             *
             * 로그인 되어 있는 유저와 요청한 데이터를 소유한 유저가 다른 경우
             *
             * @property message 접근 권한이 없습니다.
             */
            data class Forbidden(override val message: String) : HttpError {
                override val code = 403
            }

            /**
             * ## 조회할 수 없는 경우
             *
             * @property message
             * - 등록된 테마가 없습니다.
             * - 등록된 힌트가 없습니다.
             * - 존재하지 않는 테마입니다.
             * - 존재하지 않는 힌트입니다.
             * - 존재하지 않는 업체입니다.
             */
            data class NotFound(override val message: String) : HttpError {
                override val code = 404
            }

            /**
             * ## 동일한 정보 조회
             *
             * @property message 테마 내 같은 힌트코드를 가진 힌트가 존재합니다.
             */
            data class Conflict(override val message: String) : HttpError {
                override val code = 409
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
        data class OperationError(val throwable: Throwable) : Failure
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

inline fun <T> Result<T>.onFinally(action: (Result<T>) -> Unit): Result<T> {
    action(this)
    return this
}

fun <T, R> Result<T>.mapOnSuccess(map: (T) -> R): Result<R> {
    return if (isSuccessful) Result.Success(map(getOrThrow)) else failureOrThrow
}

suspend fun <T, R> Result<T>.suspendMapOnSuccess(map: suspend (T) -> R): Result<R> {
    return if (isSuccessful) Result.Success(map(getOrThrow)) else failureOrThrow
}

fun <A, B, R> Result<A>.concatMap(other: Result<B>, transform: (A, B) -> R): Result<R> {
    return try {
        mapOnSuccess { a ->
            other.mapOnSuccess { b ->
                transform(a, b)
            }.getOrThrow
        }
    } catch (e: Exception) {
        Result.Failure.OperationError(e)
    }
}

suspend fun <A, B, R> Result<A>.suspendConcatMap(other: Result<B>, transform: suspend (A, B) -> R): Result<R> {
    return try {
        suspendMapOnSuccess { a ->
            other.suspendMapOnSuccess { b ->
                transform(a, b)
            }.getOrThrow
        }
    } catch (e: Exception) {
        Result.Failure.OperationError(e)
    }
}
