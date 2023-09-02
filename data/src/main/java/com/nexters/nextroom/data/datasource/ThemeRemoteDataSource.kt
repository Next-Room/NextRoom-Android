package com.nexters.nextroom.data.datasource

import com.nexters.nextroom.data.network.ApiService
import com.nexters.nextroom.data.network.response.toDomain
import com.nexters.nextroom.domain.model.Result
import com.nexters.nextroom.domain.model.ThemeInfo
import com.nexters.nextroom.domain.model.mapOnSuccess
import javax.inject.Inject

class ThemeRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {

    suspend fun getThemes(adminCode: String): Result<List<ThemeInfo>> {
        return apiService.getThemes(adminCode)
            .mapOnSuccess { it.data.toDomain() }
    }
}
