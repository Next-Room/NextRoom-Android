package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.AdditionalUserInfoResponse
import com.nextroom.nextroom.domain.model.GoogleAuthResponse
import com.nextroom.nextroom.domain.model.GoogleLoginResponse
import com.nextroom.nextroom.domain.model.LoginInfo
import com.nextroom.nextroom.domain.model.Mypage
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.SubscriptionPlan
import com.nextroom.nextroom.domain.model.UserSubscribeStatus
import kotlinx.coroutines.flow.Flow

interface AdminRepository {

    val shopName: Flow<String>
    val loggedIn: Flow<Boolean>
    val authEvent: Flow<AuthEvent>

    /**
     * @return shopName
     * */
    suspend fun login(adminCode: String, password: String, emailSaveChecked: Boolean): Result<LoginInfo>
    suspend fun requestGoogleAuth(): Result<GoogleAuthResponse>
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
    suspend fun postGoogleLogin(idToken: String): Result<GoogleLoginResponse>
    suspend fun putAdditionalUserInfo(
        shopName: String,
        signupSource: String,
        signupReason: String,
        marketingTermAgreed: Boolean
    ): Result<AdditionalUserInfoResponse>

    sealed interface AuthEvent {
        data object RefreshTokenExpired : AuthEvent
    }
}
