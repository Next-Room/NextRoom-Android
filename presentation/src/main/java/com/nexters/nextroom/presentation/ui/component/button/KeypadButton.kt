package com.nexters.nextroom.presentation.ui.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.nextroom.presentation.R
import com.nexters.nextroom.presentation.ui.theme.Gray02
import com.nexters.nextroom.presentation.ui.theme.KeypadButton
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import com.nexters.nextroom.presentation.ui.theme.White

@Composable
fun NumberKeypad(
    modifier: Modifier = Modifier,
    number: Int,
    onClick: (Int) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable(interactionSource = interactionSource, indication = null) {
                onClick(number)
            },
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.Center),
            text = number.toString(),
            style = MaterialTheme.typography.KeypadButton,
            color = if (isPressed) Gray02 else White,
        )
    }
}

@Composable
fun IconKeypad(
    modifier: Modifier = Modifier,
    @DrawableRes drawableRes: Int,
    contentDescription: String? = null,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
    ) {
        Image(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.Center),
            painter = painterResource(id = drawableRes),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(if (isPressed) Gray02 else White),
            contentScale = ContentScale.Fit,
        )
    }
}

@Preview
@Composable
fun KeypadPreview() {
    NextRoomTheme {
        Surface {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.Center,
            ) {
                items(12) { i ->
                    if (i < 9) NumberKeypad(number = i + 1) {}
                    if (i == 10) NumberKeypad(number = 0) {}
                    if (i == 11) IconKeypad(drawableRes = R.drawable.ic_backspace) {}
                }
            }
        }
    }
}

@Preview
@Composable
fun NumberKeypadPreview() {
    NextRoomTheme {
        Surface {
            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                items(9) { item ->
                    NumberKeypad(number = item + 1, onClick = {})
                }
            }
        }
    }
}

@Preview
@Composable
fun IconKeypadPreview() {
    NextRoomTheme {
        Surface {
            IconKeypad(drawableRes = R.drawable.ic_backspace) {}
        }
    }
}
