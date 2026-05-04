package com.nextroom.nextroom.data.datasource

import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.ImageUploadService
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.mapOnSuccess
import java.io.File
import javax.inject.Inject

data class UploadImagesResult(
    val hintImageFileNames: List<String>,
    val answerImageFileNames: List<String>
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
                val hintFileNames = mutableListOf<String>()
                val answerFileNames = mutableListOf<String>()

                // Upload hint images
                presignedData.hintImageUrlList?.forEachIndexed { index, presignedUrl ->
                    if (index < hintImageFiles.size) {
                        val contentTypeFromUrl = extractContentType(presignedUrl)
                        val uploadSuccess = imageUploadService.uploadImage(
                            presignedUrl = presignedUrl,
                            imageFile = hintImageFiles[index],
                            contentType = contentTypeFromUrl
                        )
                        if (!uploadSuccess) {
                            return Result.Failure.UnknownError(Exception("Hint image upload failed at index $index"))
                        }
                        val fileName = extractFileNameWithoutExtension(presignedUrl)
                        hintFileNames.add(fileName)
                    }
                }

                // Upload answer images
                presignedData.answerImageUrlList?.forEachIndexed { index, presignedUrl ->
                    if (index < answerImageFiles.size) {
                        val contentTypeFromUrl = extractContentType(presignedUrl)
                        val uploadSuccess = imageUploadService.uploadImage(
                            presignedUrl = presignedUrl,
                            imageFile = answerImageFiles[index],
                            contentType = contentTypeFromUrl
                        )
                        if (!uploadSuccess) {
                            return Result.Failure.UnknownError(Exception("Answer image upload failed at index $index"))
                        }
                        val fileName = extractFileNameWithoutExtension(presignedUrl)
                        answerFileNames.add(fileName)
                    }
                }

                Result.Success(UploadImagesResult(hintFileNames, answerFileNames))
            }

            is Result.Failure -> presignedResult
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
