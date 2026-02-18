package com.nextroom.nextroom.presentation.ui.tutorial.timer.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.ui.tutorial.timer.TutorialTimerState

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TutorialTimerScreen(
    state: TutorialTimerState,
    onKeyInput: (Int) -> Unit,
    onBackspace: () -> Unit,
    onMemoClick: () -> Unit,
    onTimerLongPress: () -> Unit,
    onDismissTooltips: () -> Unit,
    onExitConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var arcCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var memoCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var keypadCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var backCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var showExitBottomSheet by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NRColor.Dark01)
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .offset(y = (-41).dp)
                .alpha(0.15f)
        )

        val arcMaxHeight = maxHeight * 0.48f - 64.dp - 8.dp
        val arcSize = if (maxWidth < arcMaxHeight) maxWidth else arcMaxHeight
        val gapAfterArc = maxHeight * 0.50f - 64.dp - arcSize

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TutorialToolbar(
                onBackLongPress = { showExitBottomSheet = true },
                onMemoClick = onMemoClick,
                onBackPositioned = { backCoords = it },
                onMemoPositioned = { memoCoords = it }
            )

            // Arc + timer text + hint info section (overlaid)
            Box(
                modifier = Modifier
                    .size(arcSize)
                    .onGloballyPositioned { arcCoords = it }
            ) {
                val interactionSource = remember { MutableInteractionSource() }

                ArcProgressCompose(
                    totalSeconds = state.totalSeconds,
                    lastSeconds = state.lastSeconds,
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onLongClick = { onTimerLongPress() },
                            onClick = {}
                        )
                )

                // Timer text + HINT + count vertically centered inside the arc
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.lastSeconds.toTimerFormat(),
                        style = NRTypo.NotoSansMono.size54,
                        color = NRColor.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.common_hint_eng),
                        style = NRTypo.Poppins.size20,
                        color = NRColor.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${state.openedHintCount}/${state.totalHintCount}",
                        style = NRTypo.Poppins.size14,
                        color = NRColor.Gray04
                    )
                }
            }

            Spacer(modifier = Modifier.height(gapAfterArc))

            Text(
                text = stringResource(R.string.game_hint_code_label),
                style = NRTypo.Poppins.size24,
                color = NRColor.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            CodeInputSection(
                code = state.currentInput,
                inputState = state.inputState,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            KeypadSection(
                onKeyClick = onKeyInput,
                onBackspaceClick = onBackspace,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 42.dp)
                    .onGloballyPositioned { keypadCoords = it }
            )
        }

        if (state.showTooltips) {
            TutorialTimerTooltipOverlay(
                arcCoords = arcCoords,
                memoCoords = memoCoords,
                keypadCoords = keypadCoords,
                backCoords = backCoords,
                onDismiss = onDismissTooltips
            )
        }

        if (showExitBottomSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showExitBottomSheet = false },
                sheetState = sheetState,
                containerColor = NRColor.Sub1,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            ) {
                TutorialExitBottomSheetContent(onExitClick = onExitConfirmed)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TutorialToolbar(
    onBackLongPress: () -> Unit,
    onMemoClick: () -> Unit,
    onBackPositioned: (LayoutCoordinates) -> Unit = {},
    onMemoPositioned: (LayoutCoordinates) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onLongClick = { onBackLongPress() },
                    onClick = {}
                )
                .padding(20.dp)
                .onGloballyPositioned { onBackPositioned(it) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_navigate_back_24),
                contentDescription = null,
                tint = NRColor.White
            )
        }

        Text(
            modifier = Modifier
                .padding(end = 20.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(NRColor.White)
                .clickable { onMemoClick() }
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .onGloballyPositioned { onMemoPositioned(it) },
            text = stringResource(R.string.memo_button),
            style = NRTypo.Poppins.size14,
            color = NRColor.Dark01
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
private fun TutorialTimerScreenPreview() {
    TutorialTimerScreen(
        state = TutorialTimerState(
            totalSeconds = 3600,
            lastSeconds = 3600,
            currentInput = "",
            openedHintCount = 0,
            totalHintCount = 3,
            showTooltips = false,
        ),
        onKeyInput = {},
        onBackspace = {},
        onMemoClick = {},
        onTimerLongPress = {},
        onDismissTooltips = {},
        onExitConfirmed = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun TutorialTimerScreenWithInputPreview() {
    TutorialTimerScreen(
        state = TutorialTimerState(
            totalSeconds = 3600,
            lastSeconds = 1800,
            currentInput = "12",
            openedHintCount = 1,
            totalHintCount = 3,
            showTooltips = false,
        ),
        onKeyInput = {},
        onBackspace = {},
        onMemoClick = {},
        onTimerLongPress = {},
        onDismissTooltips = {},
        onExitConfirmed = {},
    )
}
