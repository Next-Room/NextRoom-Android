package com.nextroom.nextroom.presentation.common.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NRLoading(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures { }
            },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = NRColor.White)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun NRLoadingPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        NRLoading(isVisible = true)
    }
}
