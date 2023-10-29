package com.nextroom.nextroom.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import com.nextroom.nextroom.data.db.AppSettings
import com.nextroom.nextroom.data.db.dataStore
import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.request.LoginRequest
import com.nextroom.nextroom.data.network.response.toDomain
import com.nextroom.nextroom.domain.model.LoginInfo
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.mapOnSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    suspend fun login(adminCode: String, password: String): Result<LoginInfo> {
        return apiService.login(LoginRequest(adminCode, password)).mapOnSuccess { it.toDomain() }
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
