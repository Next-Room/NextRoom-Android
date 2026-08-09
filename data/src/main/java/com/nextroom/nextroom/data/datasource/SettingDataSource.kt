package com.nextroom.nextroom.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import com.nextroom.nextroom.data.db.AppSettings
import com.nextroom.nextroom.data.db.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingDataSource @Inject constructor(
    private val context: Context,
) {

    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    suspend fun getAdminCode(): String {
        return data.first().adminCode
    }

    val shopName: Flow<String>
        get() = data.map { it.shopName }

    suspend fun getShopName(): String {
        return data.first().shopName
    }

    suspend fun getLatestGameCode(): Int {
        return data.first().latestGameCode
    }

    suspend fun setLatestGameCode(code: Int) {
        dataStore.updateData {
            it.copy(latestGameCode = code)
        }
    }

    suspend fun setLastLaunchDate() {
        dataStore.updateData {
            it.copy(lastLaunchDate = System.currentTimeMillis())
        }
    }

    suspend fun getLastLaunchDate(): Long {
        return data.first().lastLaunchDate
    }

    /**
     * 관리자 코드 저장과 로그인 처리
     * */
    suspend fun saveAdminInfo(adminCode: String, shopName: String) {
        dataStore.updateData {
            it.copy(
                adminCode = adminCode,
                shopName = shopName,
            )
        }
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.updateData {
            it.copy(loggedIn = loggedIn)
        }
    }

    suspend fun setEmailSaveChecked(emailSaveChecked: Boolean) {
        dataStore.updateData {
            it.copy(emailSaveChecked = emailSaveChecked)
        }
    }

    suspend fun getEmailSaveChecked(): Boolean {
        return data.first().emailSaveChecked
    }

    suspend fun saveUserEmail(userEmail: String) {
        dataStore.updateData {
            it.copy(userEmail = userEmail)
        }
    }

    suspend fun getUserEmail(): String {
        return data.first().userEmail
    }

    suspend fun setRecommendBackgroundCustomDialogHidden(time: Long) {
        dataStore.updateData { it.copy(backgroundCustomDialogHideUntil = time) }
    }

    suspend fun getRecommendBackgroundCustomDialogHiddenUntil(): Long {
        return data.first().backgroundCustomDialogHideUntil
    }

    suspend fun saveAppPassword(password: String) {
        dataStore.updateData {
            it.copy(appPassword = password)
        }
    }

    suspend fun getAppPassword() = data.first().appPassword

    suspend fun saveHasSeenGuidePopup() {
        dataStore.updateData {
            it.copy(hasSeenGuidePopup = true)
        }
    }

    suspend fun getHasSeenGuidePopup() = data.first().hasSeenGuidePopup
}
