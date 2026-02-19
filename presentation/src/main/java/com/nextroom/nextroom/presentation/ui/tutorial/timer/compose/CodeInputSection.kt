package com.nextroom.nextroom.presentation.ui.tutorial.timer.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.model.InputState

@Composable
fun CodeInputSection(
    code: String,
    inputState: InputState,
    modifier: Modifier = Modifier
) {
    val isError = inputState is InputState.Error
    val shakeOffset by animateFloatAsState(
        targetValue = if (isError) 1f else 0f,
        animationSpec = if (isError) {
            keyframes {
                durationMillis = 300
                0f at 0
                -5f at 50
                5f at 100
                -5f at 150
                5f at 200
                0f at 300
            }
        } else {
            tween(0)
        },
        label = "shakeAnimation"
    )

    Row(
        modifier = modifier.offset(x = shakeOffset.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(4) { index ->
            val char = code.getOrNull(index)?.toString() ?: ""
            val isFocused = index == code.length && code.length < 4

            CodeBox(
                char = char,
                isError = isError,
                isFocused = isFocused
            )
        }
    }
}

@Composable
private fun CodeBox(
    char: String,
    isError: Boolean,
    isFocused: Boolean
) {
    val borderColor = when {
        isError -> NRColor.Red
        isFocused -> NRColor.White
        else -> NRColor.Gray01
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = NRTypo.Poppins.size24,
            color = NRColor.White
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun CodeInputEmptyPreview() {
    CodeInputSection(
        code = "",
        inputState = InputState.Empty
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun CodeInputTypingPreview() {
    CodeInputSection(
        code = "12",
        inputState = InputState.Typing
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun CodeInputErrorPreview() {
    CodeInputSection(
        code = "1234",
        inputState = InputState.Error(0)
    )
}