package com.nextroom.nextroom.data.network

import com.nextroom.nextroom.data.datasource.TokenDataSource
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataSource: TokenDataSource,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${tokenDataSource.getAccessToken()}")
                .build(),
        )
    }
}
