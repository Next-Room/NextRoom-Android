package com.nexters.nextroom.data.repository

import com.nexters.nextroom.data.datasource.AuthDataSource
import com.nexters.nextroom.data.datasource.SettingDataSource
import com.nexters.nextroom.domain.model.Result
import com.nexters.nextroom.domain.model.mapOnSuccess
import com.nexters.nextroom.domain.model.onSuccess
import com.nexters.nextroom.domain.repository.AdminRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val settingDataSource: SettingDataSource,
) : AdminRepository {

    override val loggedIn: Flow<Boolean> = authDataSource.loggedIn

    override val shopName: Flow<String> = settingDataSource.shopName

    override suspend fun login(adminCode: String, password: String): Result<String> {
        return authDataSource.login(adminCode, password).onSuccess {
            authDataSource.saveTokens(it.accessToken, it.refreshToken)
            settingDataSource.saveAdminInfo(adminCode, it.shopName)
        }.mapOnSuccess {
            it.shopName
        }
    }

    override suspend fun logout() {
        authDataSource.logout()
    }

    override suspend fun verifyAdminCode(code: String): Boolean {
        return settingDataSource.getAdminCode() == code
    }
}
