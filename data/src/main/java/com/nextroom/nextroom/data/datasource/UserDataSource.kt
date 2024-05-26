package com.nextroom.nextroom.data.datasource

import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.domain.model.Result
import javax.inject.Inject

/**
 * 로그인 한 사용자의 정보와 관련된 DataSource
 */
class UserDataSource @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun resign(): Result<Unit> = apiService.resign()
}
