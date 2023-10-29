package com.nextroom.nextroom.data.network

import com.nextroom.nextroom.data.datasource.AuthDataSource
import com.nextroom.nextroom.data.datasource.TokenDataSource
import com.nextroom.nextroom.domain.model.mapOnSuccess
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenDataSource: TokenDataSource,
    private val apiService: ApiService,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            apiService.refreshToken(tokenDataSource.getRefreshToken())
                .onSuccess { newToken -> tokenDataSource.saveTokens(newToken, tokenDataSource.getRefreshToken()) }
                .mapOnSuccess { newToken ->
                    return@mapOnSuccess response.request.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                }.onFailure {
                    authDataSource.logout()
                }.getOrNull
        }
    }
}
