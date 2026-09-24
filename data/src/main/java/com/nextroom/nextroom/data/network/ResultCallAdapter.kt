package com.nextroom.nextroom.data.network

import com.nextroom.nextroom.domain.model.Result
import okhttp3.Request
import okio.Timeout
import org.json.JSONObject
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.UnknownHostException

internal class ResultCallAdapter<R>(
    private val successType: Type,
) : CallAdapter<R, Call<Result<R>>> {
    override fun adapt(call: Call<R>): Call<Result<R>> = ApiResultCall(call, successType)

    override fun responseType(): Type = successType
}

private class ApiResultCall<R>(
    private val delegate: Call<R>,
    private val successType: Type,
) : Call<Result<R>> {
    override fun enqueue(callback: Callback<Result<R>>) = delegate.enqueue(
        object : Callback<R> {
            override fun onResponse(call: Call<R>, response: Response<R>) {
                val result = response.toResult()
                callback.onResponse(this@ApiResultCall, Response.success(result))
            }

            override fun onFailure(call: Call<R>, t: Throwable) {
                val error = when (t) {
                    is UnknownHostException,
                    is IOException,
                    -> Result.Failure.NetworkError(t)

                    else -> Result.Failure.UnknownError(t)
                }
                callback.onResponse(this@ApiResultCall, Response.success(error))
            }

            private fun Response<R>.toResult(): Result<R> {
                return if (isSuccessful) {
                    body()?.let { body -> Result.Success(body) }
                        ?: run {
                            if (successType == Unit::class.java) { // 데이터 없음
                                @Suppress("UNCHECKED_CAST")
                                Result.Success(Unit as R)
                            } else {
                                Result.Failure.UnknownError(
                                    IllegalStateException(
                                        "Response code is ${code()} but body is null." +
                                            "If you expect response body to be null then define your API method as returning Unit:\n" +
                                            "@POST fun postSomething(): Result<Unit>",
                                    ),
                                )
                            }
                        }
                } else {
                    toFailure()
                }
            }

            private fun Response<R>.toFailure(): Result<R> {
                val errorBody = try {
                    errorBody()?.string()?.let { JSONObject(it) }
                } catch (_: Exception) {
                    null
                }
                val message = errorBody?.optString("message").orEmpty()

                /*
                 * 401 은 세션 거절 판정의 기준이므로 본문 code 가 아니라 HTTP 상태로 판단한다.
                 * 본문 code 는 서버가 정하는 값이고, 게이트웨이가 앱을 거치지 않고 직접 내는 401 은
                 * 애초에 앱의 응답 형식을 따르지 않는다. 이 경계가 틀리면 리프레시 토큰이 거절됐는데
                 * 로그아웃하지 않거나, 멀쩡한 세션을 로그아웃시킨다.
                 */
                if (code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    return Result.Failure.HttpError.Unauthorized(message)
                }

                // optInt 는 키가 없으면 0 을 주므로 그때는 HTTP 상태로 되돌린다. getInt 는 예외를 던진다.
                val code = errorBody?.optInt("code")?.takeIf { it != 0 } ?: code()
                return when (code) {
                    HttpURLConnection.HTTP_BAD_REQUEST -> Result.Failure.HttpError.BadRequest(message)
                    HttpURLConnection.HTTP_UNAUTHORIZED -> Result.Failure.HttpError.Unauthorized(message)
                    HttpURLConnection.HTTP_FORBIDDEN -> Result.Failure.HttpError.Forbidden(message)
                    HttpURLConnection.HTTP_NOT_FOUND -> Result.Failure.HttpError.NotFound(message)
                    HttpURLConnection.HTTP_CONFLICT -> Result.Failure.HttpError.Conflict(message)
                    HttpURLConnection.HTTP_INTERNAL_ERROR -> Result.Failure.HttpError.ServerError(message)
                    else -> Result.Failure.UnknownError(
                        IllegalStateException("Unhandled error code $code (HTTP ${code()}): $message"),
                    )
                }
            }
        },
    )

    override fun request(): Request = delegate.request()

    override fun clone(): Call<Result<R>> = ApiResultCall(delegate.clone(), successType)
    override fun execute(): Response<Result<R>> =
        throw UnsupportedOperationException("This adapter does not support sync execution")

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun timeout(): Timeout = delegate.timeout()
}
