package com.nextroom.nextroom.data.datasource

import com.nextroom.nextroom.data.model.ThemeBackgroundActivationId
import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.response.toDomain
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.ThemeInfo
import com.nextroom.nextroom.domain.model.mapOnSuccess
import javax.inject.Inject

class ThemeRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {

    suspend fun getThemes(): Result<List<ThemeInfo>> {
        return apiService.getThemes()
            .mapOnSuccess { it.data.toDomain() }
    }

    suspend fun putActiveThemeBackgroundImage(themeBackgroundActivationId: ThemeBackgroundActivationId): Result<Unit> {
        return apiService.putActiveThemeBackgroundImage(themeBackgroundActivationId)
    }
}
