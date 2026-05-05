package com.nextroom.nextroom.presentation.ui.manage.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NROutlinedTextField
import com.nextroom.nextroom.presentation.common.compose.NRTypo

@Composable
fun ThemeEditSheetContent(
    isAdd: Boolean,
    editingState: ThemeEditingState,
    onTitleChange: (String) -> Unit,
    onTimeLimitChange: (String?) -> Unit,
    onHintLimitChange: (String?) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    val title = if (isAdd) stringResource(R.string.theme_manage_add)
    else stringResource(R.string.theme_manage_edit)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 36.dp),
    ) {
        Text(
            text = title,
            style = NRTypo.Pretendard.size18SemiBold,
            color = NRColor.White,
        )

        Spacer(modifier = Modifier.height(24.dp))

        NROutlinedTextField(
            label = stringResource(R.string.theme_manage_field_name),
            value = editingState.title,
            onValueChange = onTitleChange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        NROutlinedTextField(
            label = stringResource(R.string.theme_manage_field_time_limit),
            value = editingState.timeLimit?.toString() ?: "",
            onValueChange = { onTimeLimitChange(it) },
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        NROutlinedTextField(
            label = stringResource(R.string.theme_manage_field_hint_limit),
            value = editingState.hintLimit?.toString() ?: "",
            onValueChange = onHintLimitChange,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
            ) {
                Text(text = stringResource(R.string.text_cancel), color = NRColor.Gray01)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (editingState.title.isBlank() || editingState.timeLimit == null || editingState.hintLimit == null)
                            NRColor.Gray02
                        else
                            NRColor.Blue
                    )
                    .clickable(
                        enabled = editingState.title.isNotBlank() && editingState.timeLimit != null && editingState.hintLimit != null,
                        onClick = onSave
                    )
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

@Preview(name = "테마 추가 바텀시트", showBackground = true)
@Composable
private fun ThemeEditSheetAddPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NRColor.Sub1)
    ) {
        ThemeEditSheetContent(
            isAdd = true,
            editingState = ThemeEditingState(),
            onTitleChange = {},
            onTimeLimitChange = {},
            onHintLimitChange = {},
            onCancel = {},
            onSave = {},
        )
    }
}

@Preview(name = "테마 수정 바텀시트", showBackground = true)
@Composable
private fun ThemeEditSheetEditPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NRColor.Sub1)
    ) {
        ThemeEditSheetContent(
            isAdd = false,
            editingState = ThemeEditingState(
                themeId = 1,
                title = "범인 찾기",
                timeLimit = 60,
                hintLimit = 5,
            ),
            onTitleChange = {},
            onTimeLimitChange = {},
            onHintLimitChange = {},
            onCancel = {},
            onSave = {},
        )
    }
}
