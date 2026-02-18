package com.nextroom.nextroom.presentation.ui.tutorial.memo

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

sealed interface TutorialDrawingTool {
    object Pen : TutorialDrawingTool
    object Eraser : TutorialDrawingTool
}

data class TutorialMemoState(
    val lastSeconds: Int = 0,
    val currentTool: TutorialDrawingTool = TutorialDrawingTool.Pen,
    val paths: List<PathData> = emptyList(),
    val clearCanvas: Boolean = false,
    val showTooltips: Boolean = false,
)

data class PathData(
    val points: List<Offset>,
    val color: Color = Color.White,
    val strokeWidth: Float = 5f
)
