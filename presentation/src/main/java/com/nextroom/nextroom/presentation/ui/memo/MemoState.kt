package com.nextroom.nextroom.presentation.ui.memo

sealed interface DrawingTool {
    object Pen : DrawingTool
    object Eraser : DrawingTool
}

data class MemoState(
    val lastSeconds: Int = 0,
    val currentTool: DrawingTool = DrawingTool.Pen,
)
