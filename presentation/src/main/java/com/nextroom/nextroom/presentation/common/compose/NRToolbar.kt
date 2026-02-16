package com.nextroom.nextroom.presentation.common.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.extension.throttleClick

@Composable
fun NRToolbar(
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    rightButtonText: String? = null,
    onRightButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (title != null) {
            Text(
                text = title,
                style = NRTypo.Poppins.size20,
                color = NRColor.White,
                textAlign = TextAlign.Center,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_navigate_back_24),
                modifier = Modifier
                    .size(64.dp)
                    .throttleClick { onBackClick?.invoke() }
                    .padding(20.dp),
                contentDescription = null,
            )

            if (rightButtonText != null) {
                Text(
                    text = rightButtonText,
                    color = NRColor.Dark01,
                    style = NRTypo.Poppins.size14,
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .background(
                            color = NRColor.White,
                            shape = RoundedCornerShape(size = 50.dp)
                        )
                        .padding(vertical = 6.dp, horizontal = 16.dp)
                        .throttleClick { onRightButtonClick?.invoke() }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun NRToolbarPreview() {
    NRToolbar(
        title = "01:23:45",
        onBackClick = {},
        rightButtonText = "MEMO",
        onRightButtonClick = {}
    )
}
