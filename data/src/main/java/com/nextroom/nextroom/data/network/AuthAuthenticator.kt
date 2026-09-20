package com.nextroom.nextroom.data.network

import com.nextroom.nextroom.data.datasource.AuthDataSource
import com.nextroom.nextroom.data.datasource.TokenDataSource
import com.nextroom.nextroom.domain.model.Result
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
        Timber.tag("AuthAuthenticator").d("Access Token EXPIRED!! try refresh...")
        return runBlocking {
            val tokenRequest: TokenRefreshRequest = run {
                val (access, refresh) = tokenDataSource.getTokenPair()
                TokenRefreshRequest(accessToken = access, refreshToken = refresh)
            }
            apiService.refreshToken(tokenRequest)
                .onSuccess { newToken -> tokenDataSource.saveTokens(newToken.data.accessToken, newToken.data.refreshToken) }
                .mapOnSuccess { newToken ->
                    Timber.tag("AuthAuthenticator").d("Refresh Token Success: $newToken")
                    return@mapOnSuccess response.request.newBuilder()
                        .header("Authorization", "Bearer ${newToken.data.accessToken}")
                        .build()
                }.onFailure { failure ->
                    if (failure.isSessionRejected()) {
                        Timber.tag("AuthAuthenticator").d("Refresh Token REJECTED!!: $failure")
                        authDataSource.logout()
                        authDataSource.emitRefreshTokenExpired()
                    } else {
                        Timber.tag("AuthAuthenticator").d("Refresh 일시적 실패, 세션 유지: $failure")
                    }
                }.getOrNull
        }
    }

    /**
     * 서버가 세션 자체를 거절했는지 여부.
     *
     * 401/400 은 리프레시 토큰이 더 이상 유효하지 않다는 서버의 답이므로 로그아웃한다.
     * 반면 네트워크 오류나 서버 장애는 리프레시 토큰이 멀쩡한데 잠깐 닿지 못한 것뿐이다.
     * 이 경우까지 로그아웃하면 잠깐의 끊김 때문에 매번 재로그인을 요구하게 되므로,
     * 해당 요청만 실패시키고 다음 요청에서 다시 재발급을 시도하게 둔다.
     */
    private fun Result.Failure.isSessionRejected(): Boolean =
        this is Result.Failure.HttpError && (code == 401 || code == 400)
}
