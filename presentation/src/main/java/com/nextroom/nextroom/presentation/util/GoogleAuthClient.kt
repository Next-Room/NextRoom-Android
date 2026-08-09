package com.nextroom.nextroom.presentation.util

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.nextroom.nextroom.presentation.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 구글 계정 선택 UI를 띄우고 id token을 받아온다.
 *
 * Credential Manager는 선택 UI를 띄우기 위해 반드시 Activity 기반 Context를 필요로 한다.
 * Application Context를 넘기면 Android 14 미만 기기에서 Play Services 폴백 경로를 타면서
 * "Failed to launch the selector UI" 오류가 발생하므로 [requestGoogleIdToken]에 Activity를 전달해야 한다.
 */
@Singleton
class GoogleAuthClient @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val credentialManager = CredentialManager.create(context)

    private val request: GetCredentialRequest by lazy {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.O_AUTH_WEB_CLIENT_ID)
            .build()

        GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    suspend fun requestGoogleIdToken(activity: Activity): String {
        val response = credentialManager.getCredential(
            context = activity,
            request = request,
        )
        return response.extractIdToken()
    }

    private fun GetCredentialResponse.extractIdToken(): String {
        val credential = credential
        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            throw IllegalStateException("unexpected type of credential")
        }

        return try {
            GoogleIdTokenCredential.createFrom(credential.data).idToken
        } catch (e: GoogleIdTokenParsingException) {
            throw IllegalStateException("received an invalid google id token response", e)
        }
    }
}
