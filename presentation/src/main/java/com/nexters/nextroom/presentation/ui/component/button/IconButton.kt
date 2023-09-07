package com.nexters.nextroom.presentation.ui.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.nexters.nextroom.presentation.R
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme

@Composable
fun ToggleIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes normalDrawableRes: Int,
    @DrawableRes selectedDrawableRes: Int,
    selected: Boolean = false,
    contentDescription: String? = null,
    onSelect: (currentSelected: Boolean) -> Unit,
) {
    IconButton(
        modifier = modifier.wrapContentSize(),
        onClick = { onSelect(selected) },
    ) {
        Image(
            modifier = modifier,
            painter = painterResource(id = if (selected) selectedDrawableRes else normalDrawableRes),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IconButtonPreview() {
    NextRoomTheme {
        Surface {
            var selected by remember { mutableStateOf(true) }
            ToggleIconButton(
                normalDrawableRes = R.drawable.ic_delete_normal,
                selectedDrawableRes = R.drawable.ic_delete_selected,
                selected = selected,
            ) {
                selected = !it
            }
        }
    }
}
