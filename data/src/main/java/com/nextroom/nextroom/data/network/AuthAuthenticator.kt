package com.nextroom.nextroom.data.network

import com.nextroom.nextroom.data.datasource.AuthDataSource
import com.nextroom.nextroom.data.datasource.TokenDataSource
import com.nextroom.nextroom.domain.model.mapOnSuccess
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.request.TokenRefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenDataSource: TokenDataSource,
    private val authDataSource: AuthDataSource,
    private val apiService: ApiService,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Timber.tag("MANGBAAM-AuthAuthenticator(authenticate)").d("Access Token EXPIRED!! try refresh...")
        return runBlocking {
            val tokenRequest: TokenRefreshRequest = run {
                val (access, refresh) = tokenDataSource.getTokenPair()
                TokenRefreshRequest(accessToken = access, refreshToken = refresh)
            }
            apiService.refreshToken(tokenRequest)
                .onSuccess { newToken -> tokenDataSource.saveTokens(newToken.data.accessToken, newToken.data.refreshToken) }
                .mapOnSuccess { newToken ->
                    Timber.tag("MANGBAAM-AuthAuthenticator").d("Refresh Token Success: $newToken")
                    return@mapOnSuccess response.request.newBuilder()
                        .header("Authorization", "Bearer ${newToken.data.accessToken}")
                        .build()
                }.onFailure {
                    Timber.tag("MANGBAAM-AuthAuthenticator").d("Refresh Token EXPIRED!!: $it")
                    authDataSource.logout()
                }.getOrNull
        }
    }
}
