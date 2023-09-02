package com.nexters.nextroom.data.datasource

import com.nexters.nextroom.data.network.ApiService
import com.nexters.nextroom.data.network.response.toDomain
import com.nexters.nextroom.domain.model.Hint
import com.nexters.nextroom.domain.model.Result
import com.nexters.nextroom.domain.model.mapOnSuccess
import javax.inject.Inject

class HintRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getHints(themeId: Int): Result<List<Hint>> {
        return apiService.getHint(themeId)
            .mapOnSuccess { it.data.toDomain() }
    }
}
