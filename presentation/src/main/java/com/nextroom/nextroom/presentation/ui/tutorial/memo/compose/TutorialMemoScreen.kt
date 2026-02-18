package com.nextroom.nextroom.presentation.ui.tutorial.memo.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRToolbar
import com.nextroom.nextroom.presentation.ui.tutorial.memo.PathData
import com.nextroom.nextroom.presentation.ui.tutorial.memo.TutorialDrawingTool
import com.nextroom.nextroom.presentation.ui.tutorial.memo.TutorialMemoState

@Composable
fun TutorialMemoScreen(
    state: TutorialMemoState,
    fromHint: Boolean,
    onBackClick: () -> Unit,
    onHintClick: () -> Unit,
    onPenClick: () -> Unit,
    onEraserClick: () -> Unit,
    onEraseAllClick: () -> Unit,
    onPathsChanged: (List<PathData>) -> Unit,
    onDismissTooltips: () -> Unit,
    modifier: Modifier = Modifier
) {
    var penCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var eraserCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var deleteCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NRColor.Dark01)
        ) {
            NRToolbar(
                title = state.lastSeconds.toTimerFormat(),
                onBackClick = onBackClick,
                rightButtonText = if (fromHint) stringResource(R.string.common_hint_eng) else null,
                onRightButtonClick = if (fromHint) onHintClick else null
            )

            // Drawing area with tool buttons overlaid at bottom-right
            Box(modifier = Modifier.weight(1f)) {
                DrawingCanvas(
                    paths = state.paths,
                    currentTool = state.currentTool,
                    clearCanvas = state.clearCanvas,
                    onPathsChanged = onPathsChanged,
                    modifier = Modifier.fillMaxSize()
                )

                // Tool buttons: vertically stacked at bottom-right
                // Matches fragment_memo.xml: guide_end=20dp, marginBottom=60dp for last button
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 60.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    ToolButton(
                        iconRes = if (state.currentTool == TutorialDrawingTool.Pen) {
                            R.drawable.ic_pen_selected
                        } else {
                            R.drawable.ic_pen_normal
                        },
                        onClick = onPenClick,
                        modifier = Modifier.onGloballyPositioned { penCoords = it }
                    )
                    ToolButton(
                        iconRes = if (state.currentTool == TutorialDrawingTool.Eraser) {
                            R.drawable.ic_eraser_selected
                        } else {
                            R.drawable.ic_eraser_normal
                        },
                        onClick = onEraserClick,
                        modifier = Modifier.onGloballyPositioned { eraserCoords = it }
                    )
                    ToolButton(
                        iconRes = R.drawable.ic_delete_normal,
                        onClick = onEraseAllClick,
                        modifier = Modifier.onGloballyPositioned { deleteCoords = it }
                    )
                }
            }
        }

        if (state.showTooltips) {
            TutorialMemoTooltipOverlay(
                penCoords = penCoords,
                eraserCoords = eraserCoords,
                deleteCoords = deleteCoords,
                onDismiss = onDismissTooltips
            )
        }
    }
}

@Composable
private fun DrawingCanvas(
    paths: List<PathData>,
    currentTool: TutorialDrawingTool,
    clearCanvas: Boolean,
    onPathsChanged: (List<PathData>) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPath by remember { mutableStateOf<PathData?>(null) }
    var localPaths by remember { mutableStateOf(paths) }

    LaunchedEffect(clearCanvas) {
        if (clearCanvas) {
            localPaths = emptyList()
            currentPath = null
        }
    }

    LaunchedEffect(paths) {
        if (paths != localPaths && !clearCanvas) {
            localPaths = paths
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(currentTool) {
                detectDragGestures(
                    onDragStart = { offset ->
                        if (currentTool == TutorialDrawingTool.Pen) {
                            currentPath = PathData(points = listOf(offset))
                        }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        if (currentTool == TutorialDrawingTool.Pen) {
                            currentPath = currentPath?.copy(
                                points = currentPath!!.points + change.position
                            )
                        } else {
                            val touchPoint = change.position
                            val eraserRadius = 30f
                            localPaths = localPaths.filter { pathData ->
                                !pathData.points.any { point ->
                                    (point - touchPoint).getDistance() < eraserRadius
                                }
                            }
                            onPathsChanged(localPaths)
                        }
                    },
                    onDragEnd = {
                        currentPath?.let {
                            if (it.points.size > 1) {
                                localPaths = localPaths + it
                                onPathsChanged(localPaths)
                            }
                        }
                        currentPath = null
                    },
                    onDragCancel = {
                        currentPath = null
                    }
                )
            }
    ) {
        // Draw saved path
        localPaths.forEach { pathData ->
            if (pathData.points.size > 1) {
                val path = Path().apply {
                    moveTo(pathData.points.first().x, pathData.points.first().y)
                    pathData.points.drop(1).forEach { point ->
                        lineTo(point.x, point.y)
                    }
                }
                drawPath(
                    path = path,
                    color = pathData.color,
                    style = Stroke(width = pathData.strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Draw current path
        currentPath?.let { pathData ->
            if (pathData.points.size > 1) {
                val path = Path().apply {
                    moveTo(pathData.points.first().x, pathData.points.first().y)
                    pathData.points.drop(1).forEach { point ->
                        lineTo(point.x, point.y)
                    }
                }
                drawPath(
                    path = path,
                    color = pathData.color,
                    style = Stroke(width = pathData.strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
private fun ToolButton(
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(50.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(50.dp)
        )
    }
}

private fun Int.toTimerFormat(): String {
    val minutes = this / 60
    val seconds = this % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun TutorialMemoScreenPenPreview() {
    TutorialMemoScreen(
        state = TutorialMemoState(
            lastSeconds = 1800,
            currentTool = TutorialDrawingTool.Pen,
            showTooltips = false,
        ),
        fromHint = false,
        onBackClick = {},
        onHintClick = {},
        onPenClick = {},
        onEraserClick = {},
        onEraseAllClick = {},
        onPathsChanged = {},
        onDismissTooltips = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun TutorialMemoScreenEraserPreview() {
    TutorialMemoScreen(
        state = TutorialMemoState(
            lastSeconds = 1800,
            currentTool = TutorialDrawingTool.Eraser,
            showTooltips = false,
        ),
        fromHint = false,
        onBackClick = {},
        onHintClick = {},
        onPenClick = {},
        onEraserClick = {},
        onEraseAllClick = {},
        onPathsChanged = {},
        onDismissTooltips = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun TutorialMemoFromHintPreview() {
    TutorialMemoScreen(
        state = TutorialMemoState(
            lastSeconds = 1800,
            currentTool = TutorialDrawingTool.Eraser,
            showTooltips = false,
        ),
        fromHint = true,
        onBackClick = {},
        onHintClick = {},
        onPenClick = {},
        onEraserClick = {},
        onEraseAllClick = {},
        onPathsChanged = {},
        onDismissTooltips = {}
    )
}
