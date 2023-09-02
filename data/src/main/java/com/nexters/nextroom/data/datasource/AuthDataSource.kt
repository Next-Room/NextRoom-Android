package com.nexters.nextroom.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import com.nexters.nextroom.data.db.AppSettings
import com.nexters.nextroom.data.db.dataStore
import com.nexters.nextroom.data.network.ApiService
import com.nexters.nextroom.data.network.request.LoginRequest
import com.nexters.nextroom.data.network.response.toDomain
import com.nexters.nextroom.domain.model.LoginInfo
import com.nexters.nextroom.domain.model.Result
import com.nexters.nextroom.domain.model.mapOnSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
) {

    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    val loggedIn: Flow<Boolean>
        get() = data.map { it.loggedIn }

    fun getAccessToken(): String = runBlocking {
        dataStore.data.first().accessToken
    }

    fun getRefreshToken(): String = runBlocking {
        dataStore.data.first().refreshToken
    }

    suspend fun login(adminCode: String, password: String): Result<LoginInfo> {
        return apiService.login(LoginRequest(adminCode, password)).mapOnSuccess { it.toDomain() }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.updateData {
            it.copy(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }
    }

    /**
     * 관리자 코드 제거와 로그아웃 처리
     * */
    suspend fun logout() {
        dataStore.updateData {
            it.copy(
                loggedIn = false,
                adminCode = "",
                shopName = "",
                accessToken = "",
                refreshToken = "",
            )
        }
    }
}
