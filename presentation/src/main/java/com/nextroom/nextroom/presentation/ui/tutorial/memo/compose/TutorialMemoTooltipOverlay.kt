package com.nextroom.nextroom.presentation.ui.tutorial.memo.compose

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
fun TutorialMemoTooltipOverlay(
    penCoords: LayoutCoordinates?,
    eraserCoords: LayoutCoordinates?,
    deleteCoords: LayoutCoordinates?,
    onDismiss: () -> Unit
) {
    var penTooltipSize by remember { mutableStateOf(IntSize.Zero) }
    var eraserTooltipSize by remember { mutableStateOf(IntSize.Zero) }
    var deleteTooltipSize by remember { mutableStateOf(IntSize.Zero) }

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
        // 툴팁 1: 펜 버튼 왼쪽에 "그리기"
        penCoords?.let { coords ->
            val pos = coords.positionInRoot()
            val buttonLeftX = pos.x - 16.dp
            val buttonCenterY = pos.y + coords.size.height / 2f
            NRTooltip(
                text = stringResource(R.string.text_tutorial_tooltip_pen),
                arrowPosition = TooltipArrowPosition.Right,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (buttonLeftX - penTooltipSize.width).roundToInt(),
                            y = (buttonCenterY - penTooltipSize.height / 2f).roundToInt()
                        )
                    }
                    .onSizeChanged { penTooltipSize = it }
            )
        }

        // 툴팁 2: 지우개 버튼 왼쪽에 "지우기"
        eraserCoords?.let { coords ->
            val pos = coords.positionInRoot()
            val buttonLeftX = pos.x - 16.dp
            val buttonCenterY = pos.y + coords.size.height / 2f
            NRTooltip(
                text = stringResource(R.string.text_tutorial_tooltip_eraser),
                arrowPosition = TooltipArrowPosition.Right,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (buttonLeftX - eraserTooltipSize.width).roundToInt(),
                            y = (buttonCenterY - eraserTooltipSize.height / 2f).roundToInt()
                        )
                    }
                    .onSizeChanged { eraserTooltipSize = it }
            )
        }

        // 툴팁 3: 삭제 버튼 왼쪽에 "전체 삭제"
        deleteCoords?.let { coords ->
            val pos = coords.positionInRoot()
            val buttonLeftX = pos.x - 16.dp
            val buttonCenterY = pos.y + coords.size.height / 2f
            NRTooltip(
                text = stringResource(R.string.text_tutorial_tooltip_erase_all),
                arrowPosition = TooltipArrowPosition.Right,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (buttonLeftX - deleteTooltipSize.width).roundToInt(),
                            y = (buttonCenterY - deleteTooltipSize.height / 2f).roundToInt()
                        )
                    }
                    .onSizeChanged { deleteTooltipSize = it }
            )
        }
    }
}

// LayoutCoordinates는 Preview에서 인스턴스 생성이 불가능
@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun TutorialMemoTooltipOverlayPreview() {
    TutorialMemoTooltipOverlay(
        penCoords = null,
        eraserCoords = null,
        deleteCoords = null,
        onDismiss = {}
    )
}
