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
                    val errorBody = try {
                        errorBody()?.string()?.let { JSONObject(it) }
                    } catch (_: Exception) {
                        null
                    }
                    val message = errorBody?.getString("message") ?: ""
                    when ((errorBody?.getInt("code") ?: code()).toString().first()) {
                        '4' -> Result.Failure.HttpError.NotFound(message)
                        '5' -> Result.Failure.HttpError.ServerError(message)
                        else -> Result.Failure.UnknownError(
                            IllegalStateException(
                                "Response code is ${code()} but body is null." +
                                    "If you expect response body to be null then define your API method as returning Unit:\n" +
                                    "@POST fun postSomething(): Result<Unit>",
                            ),
                        )
                    }
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
