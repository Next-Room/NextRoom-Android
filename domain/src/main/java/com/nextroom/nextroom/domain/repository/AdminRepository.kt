package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.LoginInfo
import com.nextroom.nextroom.domain.model.Mypage
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.SubscriptionPlan
import com.nextroom.nextroom.domain.model.UserSubscribeStatus
import kotlinx.coroutines.flow.Flow

interface AdminRepository {

    val shopName: Flow<String>
    val loggedIn: Flow<Boolean>

    /**
     * @return shopName
     * */
    suspend fun login(adminCode: String, password: String, emailSaveChecked: Boolean): Result<LoginInfo>
    suspend fun logout()
    suspend fun resign(): Result<Unit>
    suspend fun verifyAdminCode(code: String): Boolean
    suspend fun getUserSubscribeStatus(): Result<UserSubscribeStatus>
    suspend fun getUserSubscribe(): Result<Mypage>
    suspend fun getEmailSaveChecked(): Boolean
    suspend fun getUserEmail(): String
    suspend fun saveAppPassword(password: String)
    suspend fun getAppPassword(): String
    suspend fun getSubscriptionPlan(): Result<SubscriptionPlan>
}
