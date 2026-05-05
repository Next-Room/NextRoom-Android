package com.nextroom.nextroom.presentation.ui.manage.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextroom.nextroom.domain.model.ThemeInfo
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRLoading
import com.nextroom.nextroom.presentation.common.compose.NRTypo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemeManageScreen(
    state: ThemeManageUiState.Loaded,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onThemeClick: (ThemeInfo) -> Unit,
    onEditClick: (ThemeInfo) -> Unit,
    onDeleteClick: (ThemeInfo) -> Unit,
    onHideSheet: () -> Unit,
    onTitleChange: (String) -> Unit,
    onTimeLimitChange: (String?) -> Unit,
    onHintLimitChange: (String?) -> Unit,
    onSaveTheme: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(NRColor.Dark01),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.theme_manage_title),
                    style = NRTypo.Pretendard.size18SemiBold,
                    color = NRColor.White,
                    textAlign = TextAlign.Center,
                )

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
        },
        containerColor = NRColor.Dark01,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.themes.isEmpty()) {
                Text(
                    text = stringResource(R.string.theme_manage_empty_guide),
                    style = NRTypo.Body.size14Regular,
                    color = NRColor.Gray01,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                    lineHeight = 22.sp,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        InfoBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp)
                        )
                    }
                    items(state.themes, key = { it.id }) { theme ->
                        ThemeItem(
                            theme = theme,
                            onThemeClick = { onThemeClick(theme) },
                            onEditClick = { onEditClick(theme) },
                            onDeleteClick = { onDeleteClick(theme) },
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 16.dp),
                            color = NRColor.Gray03
                        )
                    }
                }
            }

            NRLoading(state.isLoading)
        }
    }

    if (state.sheetType != ThemeSheetType.None) {
        ModalBottomSheet(
            onDismissRequest = onHideSheet,
            sheetState = sheetState,
            containerColor = NRColor.Sub1,
        ) {
            ThemeEditSheetContent(
                isAdd = state.sheetType == ThemeSheetType.Add,
                editingState = state.editingState,
                onTitleChange = onTitleChange,
                onTimeLimitChange = onTimeLimitChange,
                onHintLimitChange = onHintLimitChange,
                onCancel = onHideSheet,
                onSave = onSaveTheme,
            )
        }
    }
}

@Composable
private fun InfoBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(NRColor.Blue15)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.theme_manage_bullet_point),
                style = NRTypo.Pretendard.size14,
                color = NRColor.Blue,
            )
            Text(
                text = stringResource(R.string.text_theme_manage_web_info),
                style = NRTypo.Body.size12Regular,
                color = NRColor.Blue,
            )
        }
    }
}

@Composable
private fun ThemeItem(
    theme: ThemeInfo,
    onThemeClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onThemeClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = theme.title,
                style = NRTypo.Pretendard.size16SemiBold,
                color = NRColor.White,
            )
            Text(
                text = buildString {
                    append(
                        stringResource(
                            R.string.theme_manage_time_format,
                            theme.timeLimitInMinute
                        )
                    )
                    append(" · ")
                    append(stringResource(R.string.theme_manage_hint_count, theme.hintLimit))
                },
                style = NRTypo.Body.size12Regular,
                color = NRColor.Gray01,
            )
        }
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = NRColor.Gray01,
                modifier = Modifier.size(20.dp),
            )
        }
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = NRColor.Gray01,
                modifier = Modifier.size(20.dp),
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_navigate_next),
            contentDescription = null,
            tint = NRColor.Gray02,
            modifier = Modifier.size(20.dp),
        )
    }
}

// ==================== Previews ====================

@Preview(name = "테마 관리 - 비어있음", showBackground = true)
@Composable
private fun ThemeManageScreenEmptyPreview() {
    ThemeManageScreen(
        state = ThemeManageUiState.Loaded(
            themes = emptyList(),
            isLoading = false,
        ),
        onBackClick = {},
        onAddClick = {},
        onThemeClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onHideSheet = {},
        onTitleChange = {},
        onTimeLimitChange = {},
        onHintLimitChange = {},
        onSaveTheme = {},
    )
}

@Preview(name = "테마 관리 - 테마 목록", showBackground = true)
@Composable
private fun ThemeManageScreenWithDataPreview() {
    ThemeManageScreen(
        state = ThemeManageUiState.Loaded(
            themes = listOf(
                ThemeInfo(
                    id = 1,
                    title = "범인 찾기",
                    timeLimitInMinute = 60,
                    hintLimit = 5,
                ),
                ThemeInfo(
                    id = 2,
                    title = "보물 찾기 모험",
                    timeLimitInMinute = 45,
                    hintLimit = -1,
                ),
                ThemeInfo(
                    id = 3,
                    title = "탈출 게임",
                    timeLimitInMinute = 90,
                    hintLimit = 3,
                ),
            ),
            isLoading = false,
        ),
        onBackClick = {},
        onAddClick = {},
        onThemeClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onHideSheet = {},
        onTitleChange = {},
        onTimeLimitChange = {},
        onHintLimitChange = {},
        onSaveTheme = {},
    )
}
