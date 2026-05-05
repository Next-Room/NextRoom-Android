package com.nextroom.nextroom.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ImageUploadService @Inject constructor(
    private val okHttpClient: OkHttpClient,
) {
    suspend fun uploadImage(
        presignedUrl: String,
        imageFile: File,
        contentType: String,
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val requestBody = imageFile.asRequestBody(contentType.toMediaType())
            val request = Request.Builder()
                .url(presignedUrl)
                .put(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
