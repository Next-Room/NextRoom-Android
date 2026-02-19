package com.nextroom.nextroom.presentation.ui.tutorial.timer.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRTypo

private val KEY_HEIGHT = 64.dp

@Composable
fun KeypadSection(
    onKeyClick: (Int) -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        KeypadRow(keys = listOf(1, 2, 3), onKeyClick = onKeyClick)
        KeypadRow(keys = listOf(4, 5, 6), onKeyClick = onKeyClick)
        KeypadRow(keys = listOf(7, 8, 9), onKeyClick = onKeyClick)
        // 마지막 줄
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(KEY_HEIGHT)
            )
            KeypadButton(
                key = 0,
                onClick = { onKeyClick(0) },
                modifier = Modifier.weight(1f)
            )
            BackspaceButton(
                onClick = onBackspaceClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KeypadRow(
    keys: List<Int>,
    onKeyClick: (Int) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        keys.forEach { key ->
            KeypadButton(
                key = key,
                onClick = { onKeyClick(key) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KeypadButton(
    key: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(KEY_HEIGHT)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = key.toString(),
            style = NRTypo.Poppins.size24,
            color = NRColor.White
        )
    }
}

@Composable
private fun BackspaceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(KEY_HEIGHT)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_backspace),
            contentDescription = null,
            tint = NRColor.White
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun KeypadPreview() {
    KeypadSection(
        onKeyClick = {},
        onBackspaceClick = {}
    )
}
