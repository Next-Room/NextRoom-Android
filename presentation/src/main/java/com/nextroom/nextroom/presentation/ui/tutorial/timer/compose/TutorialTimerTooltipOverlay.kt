package com.nextroom.nextroom.presentation.ui.tutorial.timer.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRTooltip
import com.nextroom.nextroom.presentation.common.compose.TooltipArrowPosition
import kotlin.math.roundToInt

@Composable
fun TutorialTimerTooltipOverlay(
    arcCoords: LayoutCoordinates?,
    memoCoords: LayoutCoordinates?,
    keypadCoords: LayoutCoordinates?,
    backCoords: LayoutCoordinates?,
    onDismiss: () -> Unit
) {
    var arcTooltipWidth by remember { mutableIntStateOf(0) }
    var memoTooltipWidth by remember { mutableIntStateOf(0) }
    var keypadTooltipWidth by remember { mutableIntStateOf(0) }

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
        // 툴팁 1: Arc Progress - 아크 아래 중앙에 표시
        arcCoords?.let { coords ->
            val pos = coords.positionInRoot()
            val arcCenterX = pos.x + coords.size.width / 2f
            val arcBottomY = pos.y + coords.size.height * 0.72f
            NRTooltip(
                text = stringResource(R.string.text_tutorial_tooltip_progress),
                arrowPosition = TooltipArrowPosition.Top,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (arcCenterX - arcTooltipWidth / 2f).roundToInt(),
                            y = arcBottomY.roundToInt()
                        )
                    }
                    .onSizeChanged { arcTooltipWidth = it.width }
            )
        }

        // 툴팁 2: Memo 버튼 - 메모 버튼 아래에 표시, 버블 우측을 버튼 우측에 맞춤
        memoCoords?.let { coords ->
            val pos = coords.positionInRoot()
            val memoRightX = pos.x + coords.size.width
            val memoBottomY = pos.y + coords.size.height
            NRTooltip(
                text = stringResource(R.string.text_tutorial_tooltip_memo),
                arrowPosition = TooltipArrowPosition.TopRight,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (memoRightX - memoTooltipWidth).roundToInt(),
                            y = (memoBottomY + 4.dp.toPx()).roundToInt()
                        )
                    }
                    .onSizeChanged { memoTooltipWidth = it.width }
            )
        }

        // 툴팁 3: 키패드 - 키패드 위 중앙에 표시
        keypadCoords?.let { coords ->
            val pos = coords.positionInRoot()
            val keypadCenterX = pos.x + coords.size.width / 2f
            val keypadTopY = pos.y
            NRTooltip(
                text = stringResource(R.string.text_tutorial_tooltip_keypad),
                arrowPosition = TooltipArrowPosition.Bottom,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (keypadCenterX - keypadTooltipWidth / 2f).roundToInt(),
                            y = (keypadTopY - 52.dp.toPx()).roundToInt()
                        )
                    }
                    .onSizeChanged { keypadTooltipWidth = it.width }
            )
        }

        // 툴팁 4: 뒤로가기 버튼 - 버튼 아래에 표시, 버블 좌측을 버튼 좌측에 맞춤
        backCoords?.let { coords ->
            val pos = coords.positionInRoot()
            val backLeftX = pos.x
            val backBottomY = pos.y + coords.size.height
            NRTooltip(
                text = stringResource(R.string.text_tutorial_tooltip_back),
                arrowPosition = TooltipArrowPosition.TopLeft,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = backLeftX.roundToInt(),
                            y = (backBottomY + 4.dp.toPx()).roundToInt()
                        )
                    }
            )
        }
    }
}

// LayoutCoordinates는 Preview에서 인스턴스 생성이 불가능
@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun TutorialTimerTooltipOverlayPreview() {
    TutorialTimerTooltipOverlay(
        arcCoords = null,
        memoCoords = null,
        keypadCoords = null,
        backCoords = null,
        onDismiss = {}
    )
}
