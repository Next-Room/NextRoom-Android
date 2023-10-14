package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface AdminRepository {

    val shopName: Flow<String>
    val loggedIn: Flow<Boolean>

    /**
     * @return shopName
     * */
    suspend fun login(adminCode: String, password: String): Result<String>
    suspend fun logout()
    suspend fun verifyAdminCode(code: String): Boolean
}
