package com.nextroom.nextroom.presentation.ui.tutorial.hint.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.extension.throttleClick
import com.nextroom.nextroom.presentation.ui.tutorial.TutorialData
import com.nextroom.nextroom.presentation.ui.tutorial.hint.TutorialHintState

@Composable
fun TutorialHintScreen(
    state: TutorialHintState,
    onHintOpenClick: () -> Unit,
    onAnswerOpenClick: () -> Unit,
    onHintAreaPositioned: (LayoutCoordinates) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.hint),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .width(274.dp)
                            .height(164.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.game_progress_rate),
                            style = NRTypo.Pretendard.size24,
                        )
                        Text(
                            text = "${state.hint.progress}%",
                            style = NRTypo.Pretendard.size32,
                            color = NRColor.Gray01,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    HorizontalDivider(
                        color = NRColor.Gray02,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 28.dp)
                    )

                    Text(
                        text = stringResource(R.string.common_hint),
                        style = NRTypo.Pretendard.size20,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .heightIn(min = if (state.isHintOpened) 0.dp else 200.dp)
                            .padding(top = 12.dp)
                            .onGloballyPositioned { onHintAreaPositioned(it) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .blur(if (state.isHintOpened) 0.dp else 10.dp)
                        ) {
                            Text(
                                text = state.hint.hint,
                                style = NRTypo.Pretendard.size20,
                                color = NRColor.Gray01,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (!state.isHintOpened) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(NRColor.Black.copy(alpha = 0.1f))
                                    .throttleClick { onHintOpenClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                ) {
                                    Image(
                                        modifier = Modifier.size(20.dp),
                                        painter = painterResource(R.drawable.ic_lock),
                                        colorFilter = ColorFilter.tint(NRColor.White),
                                        contentDescription = null,
                                    )
                                    Text(
                                        text = stringResource(R.string.text_open_hint_guide_message),
                                        color = NRColor.White,
                                        style = NRTypo.Pretendard.size14SemiBold,
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.game_view_hint),
                                        color = NRColor.Black,
                                        style = NRTypo.Pretendard.size16Bold,
                                        modifier = Modifier
                                            .padding(top = 20.dp)
                                            .background(
                                                color = NRColor.White,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(vertical = 8.dp, horizontal = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (state.isHintOpened) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HorizontalDivider(
                            color = NRColor.Gray02,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 28.dp)
                        )

                        Text(
                            text = stringResource(R.string.game_answer),
                            style = NRTypo.Pretendard.size20,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .heightIn(min = if (state.isAnswerOpened) 0.dp else 200.dp)
                                .padding(top = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .blur(if (state.isAnswerOpened) 0.dp else 10.dp)
                            ) {
                                Text(
                                    text = state.hint.answer,
                                    style = NRTypo.Pretendard.size20,
                                    color = NRColor.Gray01,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (!state.isAnswerOpened) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(NRColor.Black.copy(alpha = 0.1f))
                                        .throttleClick { onAnswerOpenClick() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(horizontal = 20.dp)
                                    ) {
                                        Image(
                                            modifier = Modifier.size(20.dp),
                                            painter = painterResource(R.drawable.ic_lock),
                                            colorFilter = ColorFilter.tint(NRColor.White),
                                            contentDescription = null,
                                        )
                                        Text(
                                            text = stringResource(R.string.text_open_answer_guide_message),
                                            color = NRColor.White,
                                            style = NRTypo.Pretendard.size14SemiBold,
                                            modifier = Modifier.padding(top = 10.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.game_view_answer),
                                            color = NRColor.Black,
                                            style = NRTypo.Pretendard.size16Bold,
                                            modifier = Modifier
                                                .padding(top = 20.dp)
                                                .background(
                                                    color = NRColor.White,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .padding(vertical = 8.dp, horizontal = 12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun TutorialHintScreenHintClosedPreview() {
    TutorialHintScreen(
        state = TutorialHintState(
            hint = TutorialData.hints[0],
            isHintOpened = false,
            isAnswerOpened = false
        ),
        onHintOpenClick = {},
        onAnswerOpenClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun TutorialHintScreenHintOpenedPreview() {
    TutorialHintScreen(
        state = TutorialHintState(
            hint = TutorialData.hints[1],
            isHintOpened = true,
            isAnswerOpened = false
        ),
        onHintOpenClick = {},
        onAnswerOpenClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun TutorialHintScreenAnswerOpenedPreview() {
    TutorialHintScreen(
        state = TutorialHintState(
            hint = TutorialData.hints[2],
            isHintOpened = true,
            isAnswerOpened = true
        ),
        onHintOpenClick = {},
        onAnswerOpenClick = {}
    )
}
