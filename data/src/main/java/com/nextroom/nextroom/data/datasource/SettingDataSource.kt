package com.nextroom.nextroom.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import com.nextroom.nextroom.data.db.AppSettings
import com.nextroom.nextroom.data.db.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
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

    fun setLastLaunchDate() = runBlocking {
        dataStore.updateData {
            it.copy(lastLaunchDate = System.currentTimeMillis())
        }
    }

    fun getLastLaunchDate(): Long = runBlocking {
        data.first().lastLaunchDate
    }

    fun setNetworkDisconnectedCount(count: Int) = runBlocking {
        dataStore.updateData {
            it.copy(networkDisconnectedCount = count)
        }
    }

    suspend fun getNetworkDisconnectedCount(): Int {
        return data.first().networkDisconnectedCount
    }

    /**
     * 관리자 코드 저장과 로그인 처리
     * */
    suspend fun saveAdminInfo(adminCode: String, shopName: String) {
        dataStore.updateData {
            it.copy(
                loggedIn = true,
                adminCode = adminCode,
                shopName = shopName,
            )
        }
    }


    fun setEmailSaveChecked(emailSaveChecked: Boolean) = runBlocking {
        dataStore.updateData {
            it.copy(emailSaveChecked = emailSaveChecked)
        }
    }

    fun getEmailSaveChecked() = runBlocking {
        data.first().emailSaveChecked
    }

    fun saveUserEmail(userEmail: String) = runBlocking {
        dataStore.updateData {
            it.copy(userEmail = userEmail)
        }
    }

    fun getUserEmail() = runBlocking {
        data.first().userEmail
    }

    fun setRecommendBackgroundCustomDialogHidden(time: Long) = runBlocking {
        dataStore.updateData { it.copy(backgroundCustomDialogHideUntil = time) }
    }

    fun getRecommendBackgroundCustomDialogHiddenUntil() = runBlocking {
        data.first().backgroundCustomDialogHideUntil
    }

    suspend fun saveAppPassword(password: String) {
        dataStore.updateData {
            it.copy(appPassword = password)
        }
    }

    suspend fun getAppPassword() = data.first().appPassword
}
