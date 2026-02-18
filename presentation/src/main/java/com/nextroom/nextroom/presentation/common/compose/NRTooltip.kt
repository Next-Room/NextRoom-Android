package com.nextroom.nextroom.presentation.common.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class TooltipArrowPosition { Top, TopLeft, TopRight, Bottom, Right }

@Composable
fun NRTooltip(
    text: String,
    arrowPosition: TooltipArrowPosition,
    modifier: Modifier = Modifier
) {
    val tooltipColor = NRColor.White
    val arrowWidthDp = 16.dp
    val arrowHeightDp = 8.dp

    if (arrowPosition == TooltipArrowPosition.Right) {
        Row(
            modifier = modifier.wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(color = tooltipColor, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(text = text, style = NRTypo.Body.size14Medium, color = NRColor.Dark01)
            }
            Canvas(modifier = Modifier.size(arrowHeightDp, arrowWidthDp)) {
                drawPath(
                    path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(0f, size.height)
                        lineTo(size.width, size.height / 2f)
                        close()
                    },
                    color = tooltipColor
                )
            }
        }
    } else {
        val horizontalAlignment = when (arrowPosition) {
            TooltipArrowPosition.TopLeft -> Alignment.Start
            TooltipArrowPosition.TopRight -> Alignment.End
            else -> Alignment.CenterHorizontally
        }

        Column(
            modifier = modifier.wrapContentSize(),
            horizontalAlignment = horizontalAlignment
        ) {
            if (arrowPosition == TooltipArrowPosition.Top ||
                arrowPosition == TooltipArrowPosition.TopLeft ||
                arrowPosition == TooltipArrowPosition.TopRight
            ) {
                val arrowModifier = when (arrowPosition) {
                    TooltipArrowPosition.TopLeft -> Modifier
                        .size(arrowWidthDp, arrowHeightDp)
                        .offset(x = 8.dp)
                    TooltipArrowPosition.TopRight -> Modifier
                        .size(arrowWidthDp, arrowHeightDp)
                        .offset(x = (-8).dp)
                    else -> Modifier.size(arrowWidthDp, arrowHeightDp)
                }
                Canvas(modifier = arrowModifier) {
                    drawPath(
                        path = Path().apply {
                            moveTo(size.width / 2f, 0f)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        },
                        color = tooltipColor
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(color = tooltipColor, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = text,
                    style = NRTypo.Body.size14Medium,
                    color = NRColor.Dark01,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                )
            }

            if (arrowPosition == TooltipArrowPosition.Bottom) {
                Canvas(modifier = Modifier.size(arrowWidthDp, arrowHeightDp)) {
                    drawPath(
                        path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width / 2f, size.height)
                            close()
                        },
                        color = tooltipColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun NRTooltipTopPreview() {
    NRTooltip(text = "남은 시간과 힌트 수 표시", arrowPosition = TooltipArrowPosition.Top)
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun NRTooltipTopLeftPreview() {
    NRTooltip(text = "길게 꾹 눌러 종료", arrowPosition = TooltipArrowPosition.TopLeft)
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun NRTooltipTopRightPreview() {
    NRTooltip(text = "메모 하러 이동", arrowPosition = TooltipArrowPosition.TopRight)
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun NRTooltipBottomPreview() {
    NRTooltip(text = "힌트 코드 입력", arrowPosition = TooltipArrowPosition.Bottom)
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun NRTooltipRightPreview() {
    NRTooltip(text = "그리기", arrowPosition = TooltipArrowPosition.Right)
}
