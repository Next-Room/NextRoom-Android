package com.nextroom.nextroom.data.datasource

import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.request.AddHintRequestDto
import com.nextroom.nextroom.data.network.request.EditHintRequestDto
import com.nextroom.nextroom.data.network.request.RemoveHintRequestDto
import com.nextroom.nextroom.data.network.response.toDomain
import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.mapOnSuccess
import com.nextroom.nextroom.domain.request.AddHintRequest
import com.nextroom.nextroom.domain.request.EditHintRequest
import javax.inject.Inject

class HintRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getHints(themeId: Int): Result<List<Hint>> {
        return apiService.getHint(themeId)
            .mapOnSuccess { it.data.toDomain() }
    }

    suspend fun addHint(request: AddHintRequest): Result<Unit> {
        return apiService.addHint(
            AddHintRequestDto(
                themeId = request.themeId,
                hintCode = request.hintCode,
                contents = request.contents,
                answer = request.answer,
                progress = request.progress,
            )
        )
    }

    suspend fun editHint(request: EditHintRequest): Result<Unit> {
        return apiService.editHint(
            EditHintRequestDto(
                id = request.id,
                hintCode = request.hintCode,
                contents = request.contents,
                answer = request.answer,
                progress = request.progress,
            )
        )
    }

    suspend fun deleteHint(hintId: Int): Result<Unit> {
        return apiService.deleteHint(RemoveHintRequestDto(id = hintId))
    }
}
