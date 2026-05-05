package com.nextroom.nextroom.presentation.ui.manage.hint.compose

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.ui.manage.hint.HintEditData
import com.nextroom.nextroom.presentation.ui.manage.hint.HintManageUiState
import com.nextroom.nextroom.presentation.ui.manage.hint.HintSheetType
import com.nextroom.nextroom.presentation.ui.manage.hint.HintSortType
import com.nextroom.nextroom.presentation.ui.manage.hint.UploadProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HintManageScreen(
    themeTitle: String,
    state: HintManageUiState.Loaded,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onHintClick: (Hint) -> Unit,
    onDeleteClick: (Hint) -> Unit,
    onHideSheet: () -> Unit,
    onCodeChange: (String) -> Unit,
    onContentsChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    onProgressChange: (Int?) -> Unit,
    onSaveHint: () -> Unit,
    onAddHintImages: () -> Unit,
    onAddAnswerImages: () -> Unit,
    onRemoveHintImage: (Int) -> Unit,
    onRemoveAnswerImage: (Int) -> Unit,
    onSortTypeChange: (HintSortType) -> Unit,
) {
    var showSortMenu by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            // 스와이프로 닫히지 않도록 Hidden 상태로 변경 방지
            newValue != SheetValue.Hidden
        }
    )

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(NRColor.Dark01),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.hint_manage_title),
                        style = NRTypo.Pretendard.size18SemiBold,
                        color = NRColor.White,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = themeTitle,
                        style = NRTypo.Body.size12Regular,
                        color = NRColor.Gray01,
                        textAlign = TextAlign.Center,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = NRColor.White,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box {
                            IconButton(
                                onClick = { showSortMenu = true },
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                    tint = NRColor.White,
                                )
                            }

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false },
                                modifier = Modifier.background(NRColor.Sub1),
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.hint_manage_sort_by_progress),
                                            color = if (state.sortType == HintSortType.PROGRESS) NRColor.Blue else NRColor.White,
                                            style = NRTypo.Body.size14Medium,
                                        )
                                    },
                                    onClick = {
                                        onSortTypeChange(HintSortType.PROGRESS)
                                        showSortMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.hint_manage_sort_by_code),
                                            color = if (state.sortType == HintSortType.CODE) NRColor.Blue else NRColor.White,
                                            style = NRTypo.Body.size14Medium,
                                        )
                                    },
                                    onClick = {
                                        onSortTypeChange(HintSortType.CODE)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }

                        IconButton(
                            onClick = onAddClick,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = NRColor.White,
                            )
                        }
                    }
                }
            }
        },
        containerColor = NRColor.Dark01,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.hints.isEmpty() && !state.isLoading) {
                Text(
                    text = stringResource(R.string.hint_manage_empty_guide),
                    style = NRTypo.Body.size14Regular,
                    color = NRColor.Gray01,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.sortedHints, key = { it.id }) { hint ->
                    HintItem(
                        hint = hint,
                        onHintClick = { onHintClick(hint) },
                        onDeleteClick = { onDeleteClick(hint) },
                    )
                    HorizontalDivider(color = NRColor.Gray03)
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = NRColor.Blue,
                )
            }
        }
    }

    if (state.sheetType != HintSheetType.None) {
        ModalBottomSheet(
            onDismissRequest = onHideSheet,
            sheetState = sheetState,
            containerColor = NRColor.Sub1,
            dragHandle = null,
        ) {
            HintEditSheetContent(
                isAdd = state.sheetType == HintSheetType.Add,
                editData = state.editData,
                uploadingImages = state.uploadingImages,
                uploadProgress = state.uploadProgress,
                onCodeChange = onCodeChange,
                onContentsChange = onContentsChange,
                onAnswerChange = onAnswerChange,
                onProgressChange = onProgressChange,
                onCancel = onHideSheet,
                onSave = onSaveHint,
                onAddHintImages = onAddHintImages,
                onAddAnswerImages = onAddAnswerImages,
                onRemoveHintImage = onRemoveHintImage,
                onRemoveAnswerImage = onRemoveAnswerImage,
            )
        }
    }
}

@Composable
private fun HintItem(
    hint: Hint,
    onHintClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onHintClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(NRColor.Blue15)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = hint.code,
                    style = NRTypo.Body.size14Medium,
                    color = NRColor.Blue,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = hint.description.ifBlank { stringResource(R.string.hint_manage_content_empty) },
                    style = NRTypo.Body.size14Regular,
                    color = NRColor.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (hint.answer.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.hint_manage_answer_format, hint.answer),
                        style = NRTypo.Body.size12Regular,
                        color = NRColor.Gray01,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = NRColor.Gray01,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LinearProgressIndicator(
                progress = { hint.progress / 100f },
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = NRColor.Blue,
                trackColor = NRColor.Gray03,
            )
            Text(
                text = "${hint.progress}%",
                style = NRTypo.Body.size12Regular,
                color = NRColor.Gray01,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun HintEditSheetContent(
    isAdd: Boolean,
    editData: HintEditData,
    uploadingImages: Boolean,
    uploadProgress: UploadProgress?,
    onCodeChange: (String) -> Unit,
    onContentsChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    onProgressChange: (Int?) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onAddHintImages: () -> Unit,
    onAddAnswerImages: () -> Unit,
    onRemoveHintImage: (Int) -> Unit,
    onRemoveAnswerImage: (Int) -> Unit,
) {
    val title = if (isAdd) stringResource(R.string.hint_manage_add)
    else stringResource(R.string.hint_manage_edit)

    val isSaveEnabled = editData.code.length == 4
            && editData.contents.isNotBlank()
            && editData.answer.isNotBlank()
            && !uploadingImages

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 36.dp),
            ) {
                Text(
                    text = title,
                    style = NRTypo.Pretendard.size18SemiBold,
                    color = NRColor.White,
                )

                Spacer(modifier = Modifier.height(24.dp))

                SheetTextField(
                    label = stringResource(R.string.hint_manage_field_code),
                    value = editData.code,
                    onValueChange = onCodeChange,
                    keyboardType = KeyboardType.Number,
                    placeholder = stringResource(R.string.hint_manage_field_code_placeholder),
                )

                Spacer(modifier = Modifier.height(16.dp))

                SheetTextField(
                    label = stringResource(R.string.hint_manage_field_contents),
                    value = editData.contents,
                    onValueChange = onContentsChange,
                    singleLine = false,
                    minLines = 3,
                    maxLines = 4,
                )

                Spacer(modifier = Modifier.height(16.dp))

                ImageSection(
                    label = stringResource(R.string.hint_manage_field_hint_image),
                    imageUris = editData.hintImageUris,
                    imageUrls = editData.hintImageUrls,
                    failedUriIndices = editData.failedHintImageUriIndices,
                    onAddImages = onAddHintImages,
                    onRemoveImage = onRemoveHintImage,
                )

                Spacer(modifier = Modifier.height(16.dp))

                SheetTextField(
                    label = stringResource(R.string.hint_manage_field_answer),
                    value = editData.answer,
                    onValueChange = onAnswerChange,
                    singleLine = false,
                    minLines = 2,
                    maxLines = 3,
                )

                Spacer(modifier = Modifier.height(16.dp))

                ImageSection(
                    label = stringResource(R.string.hint_manage_field_answer_image),
                    imageUris = editData.answerImageUris,
                    imageUrls = editData.answerImageUrls,
                    failedUriIndices = editData.failedAnswerImageUriIndices,
                    onAddImages = onAddAnswerImages,
                    onRemoveImage = onRemoveAnswerImage,
                )

                Spacer(modifier = Modifier.height(16.dp))

                SheetTextField(
                    label = stringResource(R.string.hint_manage_field_progress),
                    value = editData.progress?.toString() ?: "",
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() }
                        when {
                            filtered.isEmpty() -> onProgressChange(null)
                            else -> {
                                val value = filtered.toIntOrNull()
                                if (value != null && value in 0..100) {
                                    onProgressChange(value)
                                }
                                // 100 초과 시 입력 무시
                            }
                        }
                    },
                    keyboardType = KeyboardType.Number,
                    placeholder = "",
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uploadingImages && uploadProgress != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(
                                R.string.hint_manage_image_uploading,
                                uploadProgress.current,
                                uploadProgress.total
                            ),
                            style = NRTypo.Body.size12Regular,
                            color = NRColor.Gray01,
                        )
                        LinearProgressIndicator(
                            progress = { uploadProgress.current.toFloat() / uploadProgress.total },
                            modifier = Modifier.fillMaxWidth(),
                            color = NRColor.Blue,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        enabled = !uploadingImages,
                    ) {
                        Text(text = stringResource(R.string.text_cancel), color = NRColor.Gray01)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSaveEnabled) NRColor.Blue else NRColor.Gray02)
                            .clickable(enabled = isSaveEnabled, onClick = onSave)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            style = NRTypo.Pretendard.size16SemiBold,
                            color = NRColor.White,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageSection(
    label: String,
    imageUris: List<Uri>,
    imageUrls: List<String>,
    failedUriIndices: Set<Int>,
    onAddImages: () -> Unit,
    onRemoveImage: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = NRTypo.Body.size12Regular,
            color = NRColor.Gray01,
        )

        if (imageUris.isNotEmpty() || imageUrls.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(imageUris) { index, uri ->
                    ImageThumbnail(
                        imageUri = uri,
                        isFailed = index in failedUriIndices,
                        onRemove = { onRemoveImage(index) }
                    )
                }
                itemsIndexed(imageUrls) { index, url ->
                    ImageThumbnail(
                        imageUrl = url,
                        onRemove = { onRemoveImage(imageUris.size + index) }
                    )
                }
                if (imageUris.size + imageUrls.size < 5) {
                    item {
                        AddImageButton(onClick = onAddImages)
                    }
                }
            }
        } else {
            AddImageButton(onClick = onAddImages)
        }
    }
}

@Composable
private fun ImageThumbnail(
    imageUri: Uri? = null,
    imageUrl: String? = null,
    isFailed: Boolean = false,
    onRemove: () -> Unit,
) {
    Box(modifier = Modifier.size(80.dp)) {
        AsyncImage(
            model = imageUri ?: imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(NRColor.Gray03),
            contentScale = ContentScale.Crop,
        )

        if (isFailed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(NRColor.Red.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = NRColor.White,
                    modifier = Modifier.size(28.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 6.dp, y = (-6).dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(NRColor.Dark01.copy(alpha = 0.95f))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = NRColor.White,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Composable
private fun AddImageButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(NRColor.Gray03)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = NRColor.Gray01,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(R.string.hint_manage_image_add_button),
                style = NRTypo.Body.size12Regular,
                color = NRColor.Gray01,
            )
        }
    }
}

@Composable
private fun SheetTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    placeholder: String = "",
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = NRTypo.Body.size12Regular,
            color = NRColor.Gray01,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = NRTypo.Body.size14Regular.copy(color = NRColor.White),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            placeholder = if (placeholder.isNotEmpty()) {
                {
                    Text(
                        text = placeholder,
                        color = NRColor.Gray02,
                        style = NRTypo.Body.size14Regular
                    )
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NRColor.Blue,
                unfocusedBorderColor = NRColor.Gray02,
                focusedContainerColor = NRColor.Sub1,
                unfocusedContainerColor = NRColor.Sub1,
                cursorColor = NRColor.Blue,
            ),
        )
    }
}

// ==================== Previews ====================

@Preview(name = "힌트 관리 - 비어있음", showBackground = true)
@Composable
private fun HintManageScreenEmptyPreview() {
    HintManageScreen(
        themeTitle = "범인 찾기",
        state = HintManageUiState.Loaded(
            hints = emptyList(),
            isLoading = false,
            sortType = HintSortType.PROGRESS,
            uploadingImages = false,
            uploadProgress = null,
            editData = HintEditData(),
            sheetType = HintSheetType.None,
        ),
        onBackClick = {},
        onAddClick = {},
        onHintClick = {},
        onDeleteClick = {},
        onHideSheet = {},
        onCodeChange = {},
        onContentsChange = {},
        onAnswerChange = {},
        onProgressChange = {},
        onSaveHint = {},
        onAddHintImages = {},
        onAddAnswerImages = {},
        onRemoveHintImage = {},
        onRemoveAnswerImage = {},
        onSortTypeChange = {},
    )
}

@Preview(name = "힌트 관리 - 힌트 목록", showBackground = true)
@Composable
private fun HintManageScreenWithDataPreview() {
    HintManageScreen(
        themeTitle = "범인 찾기",
        state = HintManageUiState.Loaded(
            hints = listOf(
                Hint(
                    id = 1,
                    code = "1234",
                    description = "서랍 안을 살펴보세요. 열쇠가 숨겨져 있습니다.",
                    answer = "책상 서랍",
                    progress = 25,
                    hintImageUrlList = emptyList(),
                    answerImageUrlList = emptyList(),
                ),
                Hint(
                    id = 2,
                    code = "5678",
                    description = "벽에 걸린 그림을 자세히 관찰하세요.",
                    answer = "",
                    progress = 50,
                    hintImageUrlList = emptyList(),
                    answerImageUrlList = emptyList(),
                ),
                Hint(
                    id = 3,
                    code = "9012",
                    description = "책장에서 빨간색 책을 찾아보세요. 그 안에 단서가 있습니다.",
                    answer = "1945년",
                    progress = 75,
                    hintImageUrlList = emptyList(),
                    answerImageUrlList = emptyList(),
                ),
            ),
            isLoading = false,
            sortType = HintSortType.PROGRESS,
            uploadingImages = false,
            uploadProgress = null,
            editData = HintEditData(),
            sheetType = HintSheetType.None,
        ),
        onBackClick = {},
        onAddClick = {},
        onHintClick = {},
        onDeleteClick = {},
        onHideSheet = {},
        onCodeChange = {},
        onContentsChange = {},
        onAnswerChange = {},
        onProgressChange = {},
        onSaveHint = {},
        onAddHintImages = {},
        onAddAnswerImages = {},
        onRemoveHintImage = {},
        onRemoveAnswerImage = {},
        onSortTypeChange = {},
    )
}

@Preview(name = "힌트 아이템", showBackground = true)
@Composable
private fun HintItemPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NRColor.Dark01)
    ) {
        Column {
            HintItem(
                hint = Hint(
                    id = 1,
                    code = "1234",
                    description = "서랍 안을 살펴보세요. 열쇠가 숨겨져 있습니다.",
                    answer = "책상 서랍",
                    progress = 25,
                    hintImageUrlList = emptyList(),
                    answerImageUrlList = emptyList(),
                ),
                onHintClick = {},
                onDeleteClick = {},
            )
            HorizontalDivider(color = NRColor.Gray03)
            HintItem(
                hint = Hint(
                    id = 2,
                    code = "5678",
                    description = "벽에 걸린 그림을 자세히 관찰하세요.",
                    answer = "",
                    progress = 50,
                    hintImageUrlList = emptyList(),
                    answerImageUrlList = emptyList(),
                ),
                onHintClick = {},
                onDeleteClick = {},
            )
            HorizontalDivider(color = NRColor.Gray03)
            HintItem(
                hint = Hint(
                    id = 3,
                    code = "9012",
                    description = "",
                    answer = "",
                    progress = 0,
                    hintImageUrlList = emptyList(),
                    answerImageUrlList = emptyList(),
                ),
                onHintClick = {},
                onDeleteClick = {},
            )
        }
    }
}

@Preview(name = "힌트 추가 바텀시트", showBackground = true)
@Composable
private fun HintEditSheetAddPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NRColor.Sub1)
    ) {
        HintEditSheetContent(
            isAdd = true,
            editData = HintEditData(code = "12", contents = "서랍 안을 살펴보세요", progress = 25),
            uploadingImages = false,
            uploadProgress = null,
            onCodeChange = {},
            onContentsChange = {},
            onAnswerChange = {},
            onProgressChange = {},
            onCancel = {},
            onSave = {},
            onAddHintImages = {},
            onAddAnswerImages = {},
            onRemoveHintImage = {},
            onRemoveAnswerImage = {},
        )
    }
}

@Preview(name = "힌트 수정 바텀시트", showBackground = true)
@Composable
private fun HintEditSheetEditPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NRColor.Sub1)
    ) {
        HintEditSheetContent(
            isAdd = false,
            editData = HintEditData(
                code = "1234",
                contents = "서랍 안을 살펴보세요. 열쇠가 숨겨져 있습니다.",
                answer = "책상 서랍",
                progress = 25,
            ),
            uploadingImages = false,
            uploadProgress = null,
            onCodeChange = {},
            onContentsChange = {},
            onAnswerChange = {},
            onProgressChange = {},
            onCancel = {},
            onSave = {},
            onAddHintImages = {},
            onAddAnswerImages = {},
            onRemoveHintImage = {},
            onRemoveAnswerImage = {},
        )
    }
}
