package com.nextroom.nextroom.presentation.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

object ImageUtil {
    const val MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024 // 10MB

    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val fileName = getFileName(context, uri) ?: "temp_${System.currentTimeMillis()}.jpg"
            val tempFile = File(context.cacheDir, fileName)

            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null

        // Content Provider에게 파일명 물어보기
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        result = cursor.getString(index)
                    }
                }
            }
        }

        // Content Provider를 통해 파일명을 알아내지 못했을 경우 URI 경로에서 직접 추출
        if (result == null) {
            result = uri.path?.let { path ->
                val cut = path.lastIndexOf('/')
                if (cut != -1) path.substring(cut + 1) else path
            }
        }
        return result
    }

    fun getMimeType(context: Context, uri: Uri): String {
        return if (uri.scheme == "content") {
            context.contentResolver.getType(uri) ?: "image/jpeg"
        } else {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/jpeg"
        }
    }

    fun validateImageSize(context: Context, uri: Uri): Boolean {
        return try {
            val fileSize = context.contentResolver.openFileDescriptor(uri, "r")?.use {
                it.statSize
            } ?: 0
            fileSize <= MAX_IMAGE_SIZE_BYTES
        } catch (e: Exception) {
            false
        }
    }

    fun isValidImageFormat(mimeType: String): Boolean {
        return mimeType in listOf("image/jpeg", "image/jpg", "image/png", "image/webp")
    }
}
