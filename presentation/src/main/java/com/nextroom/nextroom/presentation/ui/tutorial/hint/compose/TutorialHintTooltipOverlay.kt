package com.nextroom.nextroom.presentation.ui.tutorial.hint.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRTooltip
import com.nextroom.nextroom.presentation.common.compose.TooltipArrowPosition
import com.nextroom.nextroom.presentation.extension.dp
import kotlin.math.roundToInt

@Composable
fun TutorialHintTooltipOverlay(
    hintAreaCoords: LayoutCoordinates?,
    onDismiss: () -> Unit
) {
    var tooltipSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        hintAreaCoords?.let { coords ->
            val pos = coords.positionInRoot()
            val hintAreaCenterX = pos.x + coords.size.width / 2f
            val hintAreaTopY = pos.y

            NRTooltip(
                text = stringResource(R.string.text_tutorial_tooltip_hint),
                arrowPosition = TooltipArrowPosition.Bottom,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (hintAreaCenterX - tooltipSize.width / 2f).roundToInt(),
                            y = (hintAreaTopY - tooltipSize.height - 8.dp).roundToInt()
                        )
                    }
                    .onSizeChanged { tooltipSize = it }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun TutorialHintTooltipOverlayPreview() {
    TutorialHintTooltipOverlay(
        hintAreaCoords = null,
        onDismiss = {}
    )
}
