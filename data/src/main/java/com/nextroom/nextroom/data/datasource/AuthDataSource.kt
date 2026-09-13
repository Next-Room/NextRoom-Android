package com.nextroom.nextroom.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import com.nextroom.nextroom.data.db.AppSettings
import com.nextroom.nextroom.data.db.dataStore
import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.request.LoginRequest
import com.nextroom.nextroom.domain.model.LoginInfo
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.mapOnSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val themeLocalDataSource: ThemeLocalDataSource,
) {

    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    val loggedIn: Flow<Boolean>
        get() = data.map { it.loggedIn }

    private val _authEvent = MutableSharedFlow<AdminRepository.AuthEvent>()
    val authEvent = _authEvent.asSharedFlow()

    suspend fun login(email: String, password: String): Result<LoginInfo> {
        return apiService.login(LoginRequest(email, password)).mapOnSuccess { it.data.toDomain() }
    }

    /**
     * 로그아웃 경로가 마이페이지와 리프레시 토큰 만료 두 가지이므로, 두 경우 모두 타는 이곳에서 로컬 캐시를 정리한다.
     *
     * 인증 상태 해제를 먼저 처리한다. 캐시 정리가 실패하더라도 로그아웃 자체는 성공해야 하며,
     * 특히 리프레시 토큰 만료 경로는 OkHttp Authenticator 의 runBlocking 안에서 호출되므로
     * 여기서 예외가 새어 나가면 앱이 죽는다.
     */
    suspend fun logout() {
        dataStore.updateData {
            it.copy(
                loggedIn = false,
                shopName = "",
                accessToken = "",
                refreshToken = "",
                latestGameCode = -1,
            )
        }
        runCatching {
            themeLocalDataSource.clearThemes()
        }.onFailure {
            Timber.e(it, "로그아웃 중 로컬 테마 캐시 정리 실패")
        }
    }

    suspend fun emitRefreshTokenExpired() {
        _authEvent.emit(AdminRepository.AuthEvent.RefreshTokenExpired)
    }
}
