package com.nextroom.nextroom.data.network

import com.nextroom.nextroom.data.datasource.AuthDataSource
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authDataSource: AuthDataSource,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${authDataSource.getAccessToken()}")
                .build(),
        )
    }
}
