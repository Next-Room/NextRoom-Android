package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.request.AddHintRequest
import com.nextroom.nextroom.domain.request.EditHintRequest
import java.io.File

data class UploadImagesResult(
    val hintImageFileNames: List<String>,
    val answerImageFileNames: List<String>,
    val failedHintImageIndices: Set<Int> = emptySet(),
    val failedAnswerImageIndices: Set<Int> = emptySet(),
) {
    val hasFailures: Boolean
        get() = failedHintImageIndices.isNotEmpty() || failedAnswerImageIndices.isNotEmpty()
}

interface HintRepository {
    suspend fun getHint(hintCode: String): Hint?
    suspend fun saveHints(themeId: Int): Result<Long>
    suspend fun getHintsForTheme(themeId: Int): Result<List<Hint>>
    suspend fun addHint(request: AddHintRequest): Result<Unit>
    suspend fun editHint(request: EditHintRequest): Result<Unit>
    suspend fun deleteHint(hintId: Int): Result<Unit>
    suspend fun uploadImages(
        themeId: Int,
        hintImageFiles: List<File>,
        answerImageFiles: List<File>
    ): Result<UploadImagesResult>
}
