package com.nexters.nextroom.presentation.ui.component.button

import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.nextroom.presentation.ui.theme.Dark01
import com.nexters.nextroom.presentation.ui.theme.EngSubButton
import com.nexters.nextroom.presentation.ui.theme.KorSubButton
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme

@Composable
fun SubButton(
    modifier: Modifier = Modifier,
    @StringRes stringRes: Int? = null,
    text: String = "",
    isKorean: Boolean = true,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        modifier = modifier.padding(vertical = 20.dp, horizontal = 16.dp),
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
            contentColor = Dark01,
        ),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
    ) {
        Text(
            text = stringRes?.let { id -> stringResource(id = id) } ?: text,
            style = if (isKorean) MaterialTheme.typography.KorSubButton else MaterialTheme.typography.EngSubButton,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SubButtonPreview() {
    NextRoomTheme {
        Surface {
            SubButton(text = "MEMO", isKorean = false) {}
        }
    }
}
