package com.nextroom.nextroom.data.datasource

import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.ImageUploadService
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.mapOnSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import javax.inject.Inject

data class UploadImagesResult(
    val hintImageFileNames: List<String>,
    val answerImageFileNames: List<String>,
    val failedHintImageIndices: Set<Int>,
    val failedAnswerImageIndices: Set<Int>,
)

class ImageUploadDataSource @Inject constructor(
    private val apiService: ApiService,
    private val imageUploadService: ImageUploadService,
) {
    suspend fun uploadImages(
        themeId: Int,
        hintImageFiles: List<File>,
        answerImageFiles: List<File>,
    ): Result<UploadImagesResult> {
        val presignedResult = apiService.getHintImagePresignedUrls(
            themeId = themeId,
            hintImageCount = hintImageFiles.size,
            answerImageCount = answerImageFiles.size
        ).mapOnSuccess { it.data }

        return when (presignedResult) {
            is Result.Success -> {
                val presignedData = presignedResult.data
                try {
                    coroutineScope {
                        val hintDeferred = hintImageFiles.mapIndexed { index, file ->
                            async {
                                val url = presignedData.hintImageUrlList?.getOrNull(index)
                                    ?: return@async null
                                val success = imageUploadService.uploadImage(
                                    presignedUrl = url,
                                    imageFile = file,
                                    contentType = extractContentType(url),
                                )
                                if (success) extractFileNameWithoutExtension(url) else null
                            }
                        }
                        val answerDeferred = answerImageFiles.mapIndexed { index, file ->
                            async {
                                val url = presignedData.answerImageUrlList?.getOrNull(index)
                                    ?: return@async null
                                val success = imageUploadService.uploadImage(
                                    presignedUrl = url,
                                    imageFile = file,
                                    contentType = extractContentType(url),
                                )
                                if (success) extractFileNameWithoutExtension(url) else null
                            }
                        }

                        val hintResults = hintDeferred.awaitAll()
                        val answerResults = answerDeferred.awaitAll()

                        Result.Success(
                            UploadImagesResult(
                                hintImageFileNames = hintResults.filterNotNull(),
                                answerImageFileNames = answerResults.filterNotNull(),
                                failedHintImageIndices = hintResults.indices.filter { hintResults[it] == null }
                                    .toSet(),
                                failedAnswerImageIndices = answerResults.indices.filter { answerResults[it] == null }
                                    .toSet(),
                            )
                        )
                    }
                } finally {
                    hintImageFiles.forEach { it.delete() }
                    answerImageFiles.forEach { it.delete() }
                }
            }

            is Result.Failure -> {
                hintImageFiles.forEach { it.delete() }
                answerImageFiles.forEach { it.delete() }
                presignedResult
            }
        }
    }

    private fun extractFileNameWithoutExtension(presignedUrl: String): String {
        // Presigned URL format: /.../2e90295-2093i902-4909-a945.png?...
        // Extract: 2e90295-2093i902-4909-a945
        val urlWithoutQuery = presignedUrl.split("?").first()
        val fileName = urlWithoutQuery.substringAfterLast("/")
        return fileName.substringBeforeLast(".")
    }

    private fun extractContentType(presignedUrl: String): String {
        // Extract file extension from presigned URL
        val urlWithoutQuery = presignedUrl.split("?").first()
        val extension = urlWithoutQuery.substringAfterLast(".", "")
        return when (extension.lowercase()) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "webp" -> "image/webp"
            else -> "image/jpeg" // default
        }
    }
}
