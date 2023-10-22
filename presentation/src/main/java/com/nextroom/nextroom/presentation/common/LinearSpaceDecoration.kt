package com.nextroom.nextroom.presentation.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearSpaceDecoration(
    val spaceFirst: Int = 0,
    val spaceLast: Int = 0,
    val spaceBetween: Int = 0,
) : RecyclerView.ItemDecoration() {

    constructor(space: Int) : this(space, space, space)

    constructor(spaceEnd: Int, spaceBetween: Int) : this(spaceEnd, spaceEnd, spaceBetween)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = (parent.layoutManager as? LinearLayoutManager) ?: return
        val orientation = layoutManager.orientation
        val position = parent.getChildAdapterPosition(view)
        val isReversed = layoutManager.reverseLayout xor layoutManager.stackFromEnd

        val isFirst = position == 0
        val isLast = position == state.itemCount - 1

        val firstSpace = if (isFirst) spaceFirst else spaceBetween
        val lastSpace = if (isLast) spaceLast else 0

        when (orientation) {
            RecyclerView.VERTICAL -> with(outRect) {
                top = if (isReversed) lastSpace else firstSpace
                bottom = if (isReversed) firstSpace else lastSpace
            }

            RecyclerView.HORIZONTAL -> with(outRect) {
                left = if (isReversed) lastSpace else firstSpace
                right = if (isReversed) firstSpace else lastSpace
            }
        }
    }
}
