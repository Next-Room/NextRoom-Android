package com.nextroom.nextroom.presentation.ui.manage.hint

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.domain.repository.HintRepository
import com.nextroom.nextroom.domain.repository.UploadImagesResult
import com.nextroom.nextroom.domain.request.AddHintRequest
import com.nextroom.nextroom.domain.request.EditHintRequest
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.extension.combine
import com.nextroom.nextroom.presentation.ui.manage.hint.HintManageEvent.ImageUploadFailed.Reason
import com.nextroom.nextroom.presentation.util.ImageUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HintManageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val hintRepository: HintRepository,
    @param:ApplicationContext private val context: Context,
    private val adminRepository: AdminRepository,
) : NewBaseViewModel() {

    val themeId: Int = checkNotNull(savedStateHandle["themeId"])
    val themeTitle: String = checkNotNull(savedStateHandle["themeTitle"])

    private val _hints = MutableStateFlow<List<Hint>?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _sortType = MutableStateFlow(HintSortType.PROGRESS)
    private val _uploadState = MutableStateFlow(
        UploadState(
            uploadingImages = false,
            uploadProgress = null,
        )
    )
    private val _editData = MutableStateFlow(HintEditData())
    private val _sheetType = MutableStateFlow(HintSheetType.None)

    val uiState = combine(
        _hints,
        _isLoading,
        _sortType,
        _uploadState,
        _editData,
        _sheetType,
    ) { hints, isLoading, sortType, uploadState, editData, sheetType ->
        if (hints == null) {
            HintManageUiState.PreLoading
        } else {
            HintManageUiState.Loaded(
                hints = hints,
                isLoading = isLoading,
                sortType = sortType,
                uploadingImages = uploadState.uploadingImages,
                uploadProgress = uploadState.uploadProgress,
                editData = editData,
                sheetType = sheetType,
            )
        }
    }.stateIn(baseViewModelScope, SharingStarted.Lazily, HintManageUiState.PreLoading)

    private val _uiEvent = MutableSharedFlow<HintManageEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadHints()
    }

    fun loadHints() {
        baseViewModelScope.launch {
            try {
                _isLoading.emit(true)
                hintRepository.getHintsForTheme(themeId).getOrThrow.also { hints ->
                    _hints.emit(hints)
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun showAddSheet() {
        _editData.value = HintEditData()
        _sheetType.value = HintSheetType.Add
    }

    fun showEditSheet(hint: Hint) {
        _editData.value = HintEditData(
            hintId = hint.id,
            code = hint.code,
            contents = hint.description,
            answer = hint.answer,
            progress = hint.progress,
            hintImageUrls = hint.hintImageUrlList,
            answerImageUrls = hint.answerImageUrlList,
        )
        _sheetType.value = HintSheetType.Edit
    }

    fun hideSheet() {
        _sheetType.value = HintSheetType.None
    }

    fun updateCode(code: String) {
        if (code.length <= 4 && code.all { it.isDigit() }) {
            _editData.value = _editData.value.copy(code = code)
        }
    }

    fun updateContents(contents: String) {
        _editData.value = _editData.value.copy(contents = contents)
    }

    fun updateAnswer(answer: String) {
        _editData.value = _editData.value.copy(answer = answer)
    }

    fun updateProgress(progress: Int?) {
        _editData.value = _editData.value.copy(progress = progress?.coerceIn(0, 100))
    }

    fun addHintImages(uris: List<Uri>) {
        baseViewModelScope.launch {
            val current = _editData.value
            val newList = validateAndAddImages(
                uris = uris,
                currentUris = current.hintImageUris,
                currentUrls = current.hintImageUrls,
            ) ?: return@launch
            _editData.value = current.copy(hintImageUris = newList)
        }
    }

    fun addAnswerImages(uris: List<Uri>) {
        baseViewModelScope.launch {
            val current = _editData.value
            val newList = validateAndAddImages(
                uris = uris,
                currentUris = current.answerImageUris,
                currentUrls = current.answerImageUrls,
            ) ?: return@launch
            _editData.value = current.copy(answerImageUris = newList)
        }
    }

    fun removeHintImage(index: Int) {
        val current = _editData.value
        val urisSize = current.hintImageUris.size
        val newUris: List<Uri>
        val newUrls: List<String>
        if (index < urisSize) {
            newUris = current.hintImageUris.toMutableList().also { it.removeAt(index) }
            newUrls = current.hintImageUrls
        } else {
            newUris = current.hintImageUris
            newUrls = current.hintImageUrls.toMutableList().also { it.removeAt(index - urisSize) }
        }
        _editData.value = current.copy(hintImageUris = newUris, hintImageUrls = newUrls)
    }

    fun removeAnswerImage(index: Int) {
        val current = _editData.value
        val urisSize = current.answerImageUris.size
        val newUris: List<Uri>
        val newUrls: List<String>
        if (index < urisSize) {
            newUris = current.answerImageUris.toMutableList().also { it.removeAt(index) }
            newUrls = current.answerImageUrls
        } else {
            newUris = current.answerImageUris
            newUrls = current.answerImageUrls.toMutableList().also { it.removeAt(index - urisSize) }
        }
        _editData.value = current.copy(answerImageUris = newUris, answerImageUrls = newUrls)
    }

    fun changeSortType(sortType: HintSortType) {
        baseViewModelScope.launch {
            _sortType.emit(sortType)
        }
    }

    fun saveHint() {
        val editData = _editData.value
        _sheetType.value = HintSheetType.None
        baseViewModelScope.launch {
            try {
                _isLoading.emit(true)
                val uploadedHintUrls: List<String>
                val uploadedAnswerUrls: List<String>

                if (editData.hintImageUris.isNotEmpty() || editData.answerImageUris.isNotEmpty()) {
                    val uploadResult =
                        uploadImages(editData.hintImageUris, editData.answerImageUris)
                            ?: run {
                                _isLoading.emit(false)
                                return@launch
                            }
                    uploadedHintUrls = editData.hintImageUrls + uploadResult.hintImageFileNames
                    uploadedAnswerUrls =
                        editData.answerImageUrls + uploadResult.answerImageFileNames
                    if (uploadResult.hasFailures) {
                        _editData.value = editData.copy(
                            failedHintImageUriIndices = uploadResult.failedHintImageIndices,
                            failedAnswerImageUriIndices = uploadResult.failedAnswerImageIndices,
                        )
                    }
                } else {
                    uploadedHintUrls = editData.hintImageUrls
                    uploadedAnswerUrls = editData.answerImageUrls
                }

                val result = if (editData.hintId == null) {
                    hintRepository.addHint(
                        AddHintRequest(
                            themeId = themeId,
                            hintCode = editData.code,
                            contents = editData.contents,
                            answer = editData.answer,
                            progress = editData.progress ?: 0,
                            hintImageUrlList = uploadedHintUrls,
                            answerImageUrlList = uploadedAnswerUrls,
                        )
                    )
                } else {
                    hintRepository.editHint(
                        EditHintRequest(
                            id = editData.hintId,
                            hintCode = editData.code,
                            contents = editData.contents,
                            answer = editData.answer,
                            progress = editData.progress ?: 0,
                            hintImageUrlList = uploadedHintUrls,
                            answerImageUrlList = uploadedAnswerUrls,
                        )
                    )
                }
                result.onSuccess {
                    loadHints()
                    _uiEvent.emit(HintManageEvent.HintSaved)
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    private var deleteTargetHint: Hint? = null

    fun requestDelete(hint: Hint) {
        deleteTargetHint = hint
        baseViewModelScope.launch {
            _uiEvent.emit(HintManageEvent.RequestDeleteHint(hint))
        }
    }

    fun confirmDelete() {
        val hintId = deleteTargetHint?.id ?: return
        deleteTargetHint = null
        baseViewModelScope.launch {
            try {
                _isLoading.emit(true)
                hintRepository.deleteHint(hintId).getOrThrow.also {
                    loadHints()
                    _uiEvent.emit(HintManageEvent.HintDeleted)
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    private suspend fun validateAndAddImages(
        uris: List<Uri>,
        currentUris: List<Uri>,
        currentUrls: List<String>,
    ): List<Uri>? {
        when (adminRepository.cachedSubscribeStatus) {
            SubscribeStatus.Default,
            SubscribeStatus.SUBSCRIPTION_EXPIRATION -> {
                _uiEvent.emit(HintManageEvent.ImageUploadFailed(Reason.NOT_SUBSCRIBE))
                return null
            }

            SubscribeStatus.Subscribed -> Unit
        }

        val validUris = uris.filter { uri ->
            val mimeType = ImageUtil.getMimeType(context, uri)
            val validFormat = ImageUtil.isValidImageFormat(mimeType)
            val validSize = ImageUtil.validateImageSize(context, uri)

            if (!validFormat) _uiEvent.emit(HintManageEvent.ImageUploadFailed(Reason.INVALID_FORMAT))
            if (!validSize) _uiEvent.emit(HintManageEvent.ImageUploadFailed(Reason.INVALID_SIZE))

            validFormat && validSize
        }

        val totalImages = currentUrls.size + currentUris.size + validUris.size
        return if (totalImages > 5) {
            _uiEvent.emit(HintManageEvent.ImageUploadFailed(Reason.EXCEED_IMAGE_COUNT))
            null
        } else {
            currentUris + validUris
        }
    }

    private suspend fun uploadImages(
        hintImageUris: List<Uri>,
        answerImageUris: List<Uri>,
    ): UploadImagesResult? {
        _uploadState.emit(_uploadState.value.copy(uploadingImages = true))

        val hintFiles = hintImageUris.mapNotNull { uri ->
            ImageUtil.uriToFile(context, uri)
        }

        val answerFiles = answerImageUris.mapNotNull { uri ->
            ImageUtil.uriToFile(context, uri)
        }

        if (hintFiles.size != hintImageUris.size || answerFiles.size != answerImageUris.size) {
            _uploadState.emit(
                _uploadState.value.copy(
                    uploadingImages = false,
                    uploadProgress = null
                )
            )
            _uiEvent.emit(HintManageEvent.ImageUploadFailed(Reason.CONVERT_FAIL))
            return null
        }

        return when (val result = hintRepository.uploadImages(
            themeId = themeId,
            hintImageFiles = hintFiles,
            answerImageFiles = answerFiles
        )) {
            is Result.Success -> {
                _uploadState.emit(
                    _uploadState.value.copy(
                        uploadingImages = false,
                        uploadProgress = null
                    )
                )
                result.data
            }

            is Result.Failure -> {
                _uploadState.emit(
                    _uploadState.value.copy(
                        uploadingImages = false,
                        uploadProgress = null
                    )
                )
                _uiEvent.emit(HintManageEvent.ImageUploadFailed(Reason.UPLOAD_FAIL))
                null
            }
        }
    }
}
