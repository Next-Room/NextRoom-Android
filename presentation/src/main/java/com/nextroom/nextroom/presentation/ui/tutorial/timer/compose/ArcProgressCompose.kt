package com.nextroom.nextroom.presentation.ui.tutorial.timer.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextroom.nextroom.presentation.common.compose.NRColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcProgressCompose(
    totalSeconds: Int,
    lastSeconds: Int,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val arcAngle = 270f
    val strokeWidthDp = 6.dp

    val textStyle = TextStyle(
        color = NRColor.Gray04,
        fontSize = 14.sp
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = strokeWidthDp.toPx()

        val padding = strokeWidth / 2 + 12.dp.toPx()
        val rectWidth = size.width - padding * 2
        val rectHeight = size.height - padding * 2 - strokeWidth / 2
        val rectTopLeft = Offset(padding, padding)
        val rectSize = Size(rectWidth, rectHeight)

        val centerX = rectTopLeft.x + rectWidth / 2
        val centerY = rectTopLeft.y + rectHeight / 2
        val radius = rectWidth / 2

        val startAngle = 270f - arcAngle / 2f // 135°
        val endAngle = 270f + arcAngle / 2f // 405° (= 45°)
        val progress = if (totalSeconds > 0) lastSeconds.toFloat() / totalSeconds else 0f
        val sweepStartAngle = startAngle + arcAngle * (1 - progress)

        // 지나간 progress track
        drawArc(
            color = NRColor.Gray01,
            startAngle = startAngle,
            sweepAngle = arcAngle,
            useCenter = false,
            topLeft = rectTopLeft,
            size = rectSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // 남아있는 progress track
        if (totalSeconds > 0) {
            drawArc(
                color = NRColor.White,
                startAngle = sweepStartAngle,
                sweepAngle = arcAngle * progress,
                useCenter = false,
                topLeft = rectTopLeft,
                size = rectSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // 현재 progress 위치에 handle을 그린다.
        val handleAngle = Math.toRadians((360 - sweepStartAngle).toDouble()).toFloat()
        val handleX = centerX + cos(handleAngle) * radius
        val handleY = centerY - sin(handleAngle) * radius
        drawCircle(
            color = NRColor.White,
            radius = 20f,
            center = Offset(handleX, handleY)
        )

        val startTextLayout = textMeasurer.measure("Start", textStyle)
        val endTextLayout = textMeasurer.measure("End", textStyle)

        val edgeStartAngle = Math.toRadians(startAngle.toDouble()).toFloat()
        val edgeEndAngle = Math.toRadians(endAngle.toDouble()).toFloat()

        val textSpace = 16.dp.toPx()
        val textY = centerY + sin(edgeStartAngle) * radius + startTextLayout.size.height

        // Start 텍스트
        val startX = centerX + cos(edgeStartAngle) * radius + textSpace
        drawText(
            textLayoutResult = startTextLayout,
            topLeft = Offset(startX, textY - startTextLayout.size.height)
        )

        // End 텍스트
        val endX = centerX + cos(edgeEndAngle) * radius - endTextLayout.size.width - textSpace
        drawText(
            textLayoutResult = endTextLayout,
            topLeft = Offset(endX, textY - endTextLayout.size.height)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun ArcProgressFullPreview() {
    ArcProgressCompose(
        totalSeconds = 3600,
        lastSeconds = 3600,
        modifier = Modifier.size(300.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun ArcProgressHalfPreview() {
    ArcProgressCompose(
        totalSeconds = 3600,
        lastSeconds = 1800,
        modifier = Modifier.size(300.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun ArcProgressEmptyPreview() {
    ArcProgressCompose(
        totalSeconds = 3600,
        lastSeconds = 0,
        modifier = Modifier.size(300.dp)
    )
}
