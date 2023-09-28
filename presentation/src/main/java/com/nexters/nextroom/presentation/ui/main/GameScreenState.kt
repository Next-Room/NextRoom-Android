package com.nexters.nextroom.presentation.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.nextroom.presentation.R
import com.nexters.nextroom.presentation.model.InputState
import com.nexters.nextroom.presentation.ui.hint.HintState

data class GameScreenState(
    val totalSeconds: Int = 0,
    val lastSeconds: Int = 0,
    val currentInput: String = "",
    val inputState: InputState = InputState.Empty,
    val usedHints: Set<Int> = emptySet(),
    val answerOpenedHints: Set<Int> = emptySet(),
    val totalHintCount: Int = -1,
    val currentHint: HintState? = null,
) {
    val usedHintsCount: Int
        get() = usedHints.size

    val answerOpened: Boolean
        get() = answerOpenedHints.contains(currentHint?.hintId)

    val composables: List<HintScreenComposable>
        get() = mutableListOf(
            HintScreenComposable.Image(R.drawable.hint, top = 30.dp),
            HintScreenComposable.Title(R.string.game_progress_rate, top = 48.dp),
            HintScreenComposable.Progress(currentHint?.progress ?: 0, top = 12.dp),
            HintScreenComposable.Divider,
            HintScreenComposable.Title(R.string.common_hint),
            HintScreenComposable.Content(currentHint?.hint ?: "", top = 12.dp),
        ).apply {
            if (answerOpened) {
                add(HintScreenComposable.Divider)
                add(HintScreenComposable.Title(R.string.game_answer))
                add(HintScreenComposable.Content(currentHint?.answer ?: "", top = 12.dp))
            }
        }
}

sealed interface HintScreenComposable {
    val top: Dp
    val bottom: Dp

    data class Image(
        @DrawableRes val drawableRes: Int,
        override val top: Dp = 0.dp,
        override val bottom: Dp = 0.dp,
    ) : HintScreenComposable

    data object Divider : HintScreenComposable {
        override val top: Dp = 24.dp
        override val bottom: Dp = 24.dp
    }

    data class Title(
        val stringRes: Int,
        override val top: Dp = 0.dp,
        override val bottom: Dp = 0.dp,
    ) : HintScreenComposable

    data class Content(
        val text: String,
        override val top: Dp = 0.dp,
        override val bottom: Dp = 0.dp,
    ) : HintScreenComposable

    data class Progress(
        val progress: Int,
        override val top: Dp = 0.dp,
        override val bottom: Dp = 0.dp,
    ) : HintScreenComposable
}
