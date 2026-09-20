package com.nextroom.nextroom.data.network

import com.nextroom.nextroom.data.datasource.AuthDataSource
import com.nextroom.nextroom.data.datasource.TokenDataSource
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.mapOnSuccess
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.request.TokenRefreshRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    /**
     * 재발급을 한 번에 하나만 수행하기 위한 잠금.
     *
     * 액세스 토큰이 만료된 시점에 요청이 여러 개 떠 있으면 모두 401 을 받고
     * 각자 [authenticate] 로 들어온다. 잠금이 없으면 모두 같은 리프레시 토큰으로
     * 재발급을 요청하는데, 서버가 리프레시 토큰을 한 번 쓰고 폐기하는 방식이면
     * 먼저 도착한 하나만 성공하고 나머지는 이미 무효해진 토큰을 내밀어 401 을 받는다.
     * 그 결과 재발급에 성공했는데도 세션이 날아간다.
     */
    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        Timber.tag("AuthAuthenticator").d("Access Token EXPIRED!! try refresh...")
        return runBlocking {
            refreshMutex.withLock {
                val (access, refresh) = tokenDataSource.getTokenPair()

                // 잠금을 기다리는 동안 다른 요청이 이미 갱신했다면 재발급 없이 새 토큰으로 재시도만 한다.
                if (access.isNotEmpty() && access != response.usedAccessToken()) {
                    Timber.tag("AuthAuthenticator").d("이미 갱신된 토큰이 있어 재발급을 생략한다")
                    return@withLock response.retryWith(access)
                }

                // 로그아웃 직후처럼 리프레시 토큰이 없으면 재발급할 것이 없다.
                if (refresh.isEmpty()) {
                    Timber.tag("AuthAuthenticator").d("리프레시 토큰이 없어 재발급하지 않는다")
                    return@withLock null
                }

                apiService.refreshToken(TokenRefreshRequest(accessToken = access, refreshToken = refresh))
                    .onSuccess { newToken -> tokenDataSource.saveTokens(newToken.data.accessToken, newToken.data.refreshToken) }
                    .mapOnSuccess { newToken ->
                        Timber.tag("AuthAuthenticator").d("Refresh Token Success: $newToken")
                        return@mapOnSuccess response.retryWith(newToken.data.accessToken)
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
    }

    /** 401 을 받은 요청이 실제로 달고 나갔던 액세스 토큰. */
    private fun Response.usedAccessToken(): String? =
        request.header(AUTHORIZATION)?.removePrefix(BEARER_PREFIX)

    private fun Response.retryWith(accessToken: String): Request =
        request.newBuilder()
            .header(AUTHORIZATION, "$BEARER_PREFIX$accessToken")
            .build()

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

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
}
