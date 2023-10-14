package com.nextroom.nextroom.data.datasource

import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.response.toDomain
import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.mapOnSuccess
import javax.inject.Inject

class HintRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getHints(themeId: Int): Result<List<Hint>> {
        return apiService.getHint(themeId)
            .mapOnSuccess { it.data.toDomain() }
    }
}
