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
                hintImageList = request.hintImageUrlList.map { extractImageKey(it) },
                answerImageList = request.answerImageUrlList.map { extractImageKey(it) },
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
                hintImageList = request.hintImageUrlList.map { extractImageKey(it) },
                answerImageList = request.answerImageUrlList.map { extractImageKey(it) },
            )
        )
    }

    // Pre-signed URL에서 파일 key(UUID, 확장자 제외)만 추출
    // 서버는 항상 UUID만 받아야 하는데, 기존 이미지의 경우 pre-signed URL이 그대로 전달될 수 있음
    // 이미지 리스트는 확장자를 제외한 순수 파일 이름 필요
    private fun extractImageKey(urlOrKey: String): String {
        if (!urlOrKey.startsWith("http")) return urlOrKey
        val pathWithoutQuery = urlOrKey.split("?").first()
        val fileName = pathWithoutQuery.substringAfterLast("/")
        return fileName.substringBeforeLast(".")
    }

    suspend fun deleteHint(hintId: Int): Result<Unit> {
        return apiService.deleteHint(RemoveHintRequestDto(id = hintId))
    }
}
