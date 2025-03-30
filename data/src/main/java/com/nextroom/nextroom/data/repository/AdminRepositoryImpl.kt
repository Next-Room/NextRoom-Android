package com.nextroom.nextroom.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.nextroom.nextroom.data.datasource.AuthDataSource
import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.data.datasource.SubscriptionDataSource
import com.nextroom.nextroom.data.datasource.TokenDataSource
import com.nextroom.nextroom.data.datasource.UserDataSource
import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.response.AdditionalUserInfoRequestDto
import com.nextroom.nextroom.data.network.response.GoogleLoginRequestDto
import com.nextroom.nextroom.domain.model.AdditionalUserInfoResponse
import com.nextroom.nextroom.domain.model.GoogleAuthResponse
import com.nextroom.nextroom.domain.model.GoogleLoginResponse
import com.nextroom.nextroom.domain.model.LoginInfo
import com.nextroom.nextroom.domain.model.Mypage
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.SubscriptionPlan
import com.nextroom.nextroom.domain.model.UserSubscribeStatus
import com.nextroom.nextroom.domain.model.mapOnSuccess
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
    private val getCredentialRequest: GetCredentialRequest,
    private val credentialManager: CredentialManager,
    private val context: Context,
    private val apiService: ApiService,
) : AdminRepository {

    override val loggedIn: Flow<Boolean> = authDataSource.loggedIn

    override val shopName: Flow<String> = settingDataSource.shopName

    override suspend fun login(adminCode: String, password: String, emailSaveChecked: Boolean): Result<LoginInfo> {
        return authDataSource.login(adminCode, password).onSuccess {
            if (emailSaveChecked) {
                settingDataSource.saveUserEmail(adminCode)
            }
            settingDataSource.setEmailSaveChecked(emailSaveChecked)
            settingDataSource.saveAdminInfo(adminCode = it.adminCode, shopName = it.shopName)
            settingDataSource.setLoggedIn(true)
            tokenDataSource.saveTokens(it.accessToken, it.refreshToken)
        }
    }

    override suspend fun requestGoogleAuth(): Result<GoogleAuthResponse> {
        return try {
            val result = credentialManager.getCredential(
                request = getCredentialRequest,
                context = context,
            )
            handleSignIn(result)
        } catch (e: Exception) {
            Log.d(TAG_NR, e.toString())
            Result.Failure.NetworkError(e)
        }
    }

    private fun handleSignIn(result: GetCredentialResponse): Result.Success<GoogleAuthResponse> {
        return when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        Result.Success(
                            GoogleAuthResponse(
                                idToken = googleIdTokenCredential.idToken,
                                email = googleIdTokenCredential.id,
                                name = googleIdTokenCredential.displayName,
                            )
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        throw Exception("handleSignIn received an invalid google id token response", e)
                    }
                } else {
                    throw Exception("unexpected type of credential")
                }
            }

            else -> throw Exception("unexpected type of credential")
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

    override suspend fun getEmailSaveChecked(): Boolean {
        return settingDataSource.getEmailSaveChecked()
    }

    override suspend fun getUserEmail(): String {
        return settingDataSource.getUserEmail()
    }

    override suspend fun saveAppPassword(password: String) {
        return settingDataSource.saveAppPassword(password)
    }

    override suspend fun getAppPassword(): String {
        return settingDataSource.getAppPassword()
    }

    override suspend fun getSubscriptionPlan(): Result<SubscriptionPlan> {
        return subscriptionDataSource.getSubscriptionPlan()
    }

    override suspend fun postGoogleLogin(idToken: String): Result<GoogleLoginResponse> {
        return apiService.postGoogleLogin(GoogleLoginRequestDto(idToken)).mapOnSuccess {
            it.data.toDomainModel()
        }.onSuccess {
            if (it.isComplete) settingDataSource.setLoggedIn(true)
            settingDataSource.saveAdminInfo(adminCode = it.adminCode, shopName = it.shopName ?: "")
            tokenDataSource.saveTokens(it.accessToken, it.refreshToken)
        }
    }

    override suspend fun putAdditionalUserInfo(
        shopName: String,
        signupSource: String,
        signupReason: String,
        marketingTermAgreed: Boolean
    ): Result<AdditionalUserInfoResponse> {
        return apiService.putAdditionalUserInfo(
            request = AdditionalUserInfoRequestDto(
                shopName = shopName,
                signupSource = signupSource,
                signupReason = signupReason,
                marketingTermAgreed = marketingTermAgreed
            )
        ).mapOnSuccess {
            it.data.toDomainModel()
        }.onSuccess {
            // adminCode는 서비스 내에서 제거될 예정. 현재 사용하고 있지 않고 일부 코드만이 남아있다.
            settingDataSource.saveAdminInfo(adminCode = it.adminCode ?: "", shopName = it.shopName)
            settingDataSource.setLoggedIn(true)
        }
    }

    companion object {
        const val TAG_NR = "TAG_NR"
    }
}
