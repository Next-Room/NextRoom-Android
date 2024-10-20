package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.LoginInfo
import com.nextroom.nextroom.domain.model.Mypage
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.UserSubscribeStatus
import kotlinx.coroutines.flow.Flow

interface AdminRepository {

    val shopName: Flow<String>
    val loggedIn: Flow<Boolean>

    /**
     * @return shopName
     * */
    suspend fun login(adminCode: String, password: String, idSaveChecked: Boolean): Result<LoginInfo>
    suspend fun logout()
    suspend fun resign(): Result<Unit>
    suspend fun verifyAdminCode(code: String): Boolean
    suspend fun getUserSubscribeStatus(): Result<UserSubscribeStatus>
    suspend fun getUserSubscribe(): Result<Mypage>
    suspend fun getIdSaveChecked(): Boolean
    suspend fun getUserEmail(): String
}
