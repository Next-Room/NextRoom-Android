package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.datasource.AuthDataSource
import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.data.datasource.SubscriptionDataSource
import com.nextroom.nextroom.data.datasource.TokenDataSource
import com.nextroom.nextroom.data.datasource.UserDataSource
import com.nextroom.nextroom.domain.model.LoginInfo
import com.nextroom.nextroom.domain.model.Mypage
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.UserSubscribeStatus
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource,
    private val settingDataSource: SettingDataSource,
    private val tokenDataSource: TokenDataSource,
    private val subscriptionDataSource: SubscriptionDataSource,
) : AdminRepository {

    override val loggedIn: Flow<Boolean> = authDataSource.loggedIn

    override val shopName: Flow<String> = settingDataSource.shopName

    override suspend fun login(adminCode: String, password: String): Result<LoginInfo> {
        return authDataSource.login(adminCode, password).onSuccess {
            settingDataSource.saveAdminInfo(adminCode = it.adminCode, shopName = it.shopName)
            tokenDataSource.saveTokens(it.accessToken, it.refreshToken)
        }
    }

    override suspend fun logout() {
        authDataSource.logout()
    }

    override suspend fun resign(): Result<Unit> {
        return userDataSource.resign().onSuccess {
            logout()
        }
    }

    override suspend fun verifyAdminCode(code: String): Boolean {
        return settingDataSource.getAdminCode() == code
    }

    override suspend fun getUserSubscribeStatus(): Result<UserSubscribeStatus> {
        return subscriptionDataSource.getUserSubscriptionStatus()
    }

    override suspend fun getUserSubscribe(): Result<Mypage> {
        return subscriptionDataSource.getUserSubscription()
    }
}
