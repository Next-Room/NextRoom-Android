package com.nexters.nextroom.presentation.ui.component.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nexters.nextroom.presentation.R
import com.nexters.nextroom.presentation.ui.theme.Dark01
import com.nexters.nextroom.presentation.ui.theme.Gray01
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import com.nexters.nextroom.presentation.ui.theme.White

@Composable
fun NRDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    @StringRes titleRes: Int? = null,
    message: String? = null,
    @StringRes messageRes: Int? = null,
    onDismissRequest: () -> Unit,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .border(width = 1.dp, color = Gray01, shape = RoundedCornerShape(8.dp))
                .padding(start = 20.dp, end = 20.dp, top = 36.dp, bottom = 24.dp)
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            title ?: titleRes?.let { stringResource(it) }?.let {
                Text(
                    text = it,
                    Modifier.padding(bottom = 6.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            message ?: messageRes?.let { stringResource(it) }?.let {
                Text(text = it, color = Gray01)
            }
            Spacer(modifier = Modifier.size(size = 32.dp))
            Row {
                NRDialogButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.dialog_no),
                    textColor = White,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    onClick = onNegativeClick,
                )
                Spacer(modifier = Modifier.size(8.dp))
                NRDialogButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.dialog_yes),
                    textColor = Dark01,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    onClick = onPositiveClick,
                )
            }
        }
    }
}

@Composable
private fun NRDialogButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    @StringRes stringRes: Int? = null,
    textColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    TextButton(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = textColor,
            containerColor = backgroundColor,
        ),
        contentPadding = PaddingValues(vertical = 8.dp),
        onClick = onClick,
    ) {
        Text(
            text = text ?: stringRes?.let { stringResource(id = it) } ?: "",
            color = textColor,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF, widthDp = 500, heightDp = 400)
@Composable
fun NRDialogPreview() {
    NextRoomTheme {
        Surface {
            NRDialog(
                titleRes = R.string.game_main_exit_dialog,
                messageRes = R.string.game_main_exit_dialog_message,
                onDismissRequest = {},
                onPositiveClick = {},
                onNegativeClick = {},
            )
        }
    }
}
