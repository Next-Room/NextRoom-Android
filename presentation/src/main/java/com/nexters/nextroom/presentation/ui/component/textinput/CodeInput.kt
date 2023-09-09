package com.nexters.nextroom.presentation.ui.component.textinput

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.nextroom.presentation.ui.theme.Gray01
import com.nexters.nextroom.presentation.ui.theme.KeypadButton
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import com.nexters.nextroom.presentation.ui.theme.Red
import com.nexters.nextroom.presentation.ui.theme.White

@Composable
fun CodeInput(
    modifier: Modifier = Modifier,
    code: String,
    maxLength: Int = 4,
    isError: Boolean = false,
) {
    val readCode = if (code.length > maxLength) code.slice(0 until maxLength) else code
    val offset by animateFloatAsState(
        targetValue = 0f,
        animationSpec = keyframes {
            durationMillis = 500
            0f at 0
            5f at 100
            -5f at 200
            3f at 300
            -3f at 400
        },
        label = "",
    )
    Row(
        modifier = (if (isError) modifier.offset(x = offset.dp) else modifier).padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(maxLength) {
            CodeInputItem(
                code = if (it >= readCode.length) null else readCode[it],
                color = when {
                    isError -> Red
                    it in 1 until maxLength && it == readCode.length -> White
                    else -> Gray01
                },
            )
        }
    }
}

@Composable
private fun CodeInputItem(
    code: Char? = null,
    color: Color = Gray01,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(1.dp, color, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.wrapContentSize(),
            text = code?.toString() ?: "",
            color = White,
            maxLines = 1,
            style = MaterialTheme.typography.KeypadButton,
        )
    }
}

@Preview
@Composable
fun CodeInputPreview() {
    NextRoomTheme {
        Surface {
            CodeInput(code = "1234", isError = true)
        }
    }
}
