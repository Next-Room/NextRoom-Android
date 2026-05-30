package com.nextroom.nextroom.presentation.ui.manage.hint

import android.net.Uri
import com.nextroom.nextroom.domain.model.Hint

data class HintEditData(
    val hintId: Int? = null,
    val code: String = "",
    val contents: String = "",
    val answer: String = "",
    val progress: Int? = null,
    val hintImageUris: List<Uri> = emptyList(),
    val hintImageUrls: List<String> = emptyList(),
    val answerImageUris: List<Uri> = emptyList(),
    val answerImageUrls: List<String> = emptyList(),
    val failedHintImageUriIndices: Set<Int> = emptySet(),
    val failedAnswerImageUriIndices: Set<Int> = emptySet(),
)

sealed interface HintManageUiState {
    data object PreLoading : HintManageUiState
    data class Loaded(
        val hints: List<Hint>,
        val isLoading: Boolean,
        val sortType: HintSortType,
        val uploadingImages: Boolean,
        val uploadProgress: UploadProgress?,
        val editData: HintEditData,
        val sheetType: HintSheetType,
    ) : HintManageUiState {
        val sortedHints: List<Hint>
            get() = when (sortType) {
                HintSortType.PROGRESS -> hints.sortedBy { it.progress }
                HintSortType.CODE -> hints.sortedBy { it.code }
            }
    }
}

data class UploadProgress(
    val current: Int,
    val total: Int,
)

data class UploadState(
    val uploadingImages: Boolean,
    val uploadProgress: UploadProgress?,
)

enum class HintSheetType { None, Add, Edit }

enum class HintSortType {
    PROGRESS,  // 진행률순 (기본값)
    CODE       // 힌트코드순
}

sealed interface HintManageEvent {
    data class RequestDeleteHint(val hint: Hint) : HintManageEvent
    data object HintSaved : HintManageEvent
    data object HintDeleted : HintManageEvent
    data object SaveBlockedDueToImages : HintManageEvent
    data class ImageUploadFailed(val reason: Reason) : HintManageEvent {
        enum class Reason {
            INVALID_FORMAT,
            INVALID_SIZE,
            EXCEED_IMAGE_COUNT,
            CONVERT_FAIL,
            UPLOAD_FAIL,
            NOT_SUBSCRIBE,
        }
    }
}
