package com.nexters.nextroom.data.network

import com.nexters.nextroom.data.datasource.AuthDataSource
import com.nexters.nextroom.domain.model.mapOnSuccess
import com.nexters.nextroom.domain.model.onFailure
import com.nexters.nextroom.domain.model.onSuccess
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val apiService: ApiService,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            apiService.refreshToken(authDataSource.getRefreshToken())
                .onSuccess { newToken -> authDataSource.saveTokens(newToken, authDataSource.getRefreshToken()) }
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
