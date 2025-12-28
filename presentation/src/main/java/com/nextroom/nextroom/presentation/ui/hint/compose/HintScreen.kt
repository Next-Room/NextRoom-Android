package com.nextroom.nextroom.presentation.ui.hint.compose

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRLoading
import com.nextroom.nextroom.presentation.common.compose.NRToolbar
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.extension.throttleClick
import com.nextroom.nextroom.presentation.extension.toTimerFormat
import com.nextroom.nextroom.presentation.model.Hint
import com.nextroom.nextroom.presentation.ui.hint.HintState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HintScreen(
    state: HintState,
    onHintImageClick: (Int) -> Unit,
    onAnswerImageClick: (Int) -> Unit,
    onHintOpenClick: () -> Unit,
    onAnswerOpenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = modifier.padding(horizontal = 20.dp)
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
                            modifier = Modifier
                                .fillMaxWidth()
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
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .blur(if (state.isHintOpened) 0.dp else 10.dp)
                            ) {
                                if (state.hint.hintImageUrlList.isNotEmpty() && state.isHintOpened) {
                                    ImagePager(
                                        imageUrls = state.hint.hintImageUrlList,
                                        subscribeStatus = state.userSubscribeStatus,
                                        networkDisconnectedCount = state.networkDisconnectedCount,
                                        onImageClick = onHintImageClick,
                                        modifier = Modifier.padding(bottom = 20.dp)
                                    )
                                }

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
                                            modifier = modifier.size(20.dp),
                                            painter = painterResource(R.drawable.ic_lock),
                                            colorFilter = ColorFilter.tint(NRColor.White),
                                            contentDescription = null,
                                        )
                                        Text(
                                            text = stringResource(R.string.text_open_hint_guide_message),
                                            color = NRColor.White,
                                            style = NRTypo.Pretendard.size14SemiBold,
                                            modifier = modifier
                                                .padding(top = 10.dp)
                                                .throttleClick { onHintOpenClick() }
                                        )
                                        Text(
                                            text = stringResource(R.string.game_view_hint),
                                            color = NRColor.Black,
                                            style = NRTypo.Pretendard.size16Bold,
                                            modifier = modifier
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
                                    if (state.hint.answerImageUrlList.isNotEmpty() && state.isAnswerOpened) {
                                        ImagePager(
                                            imageUrls = state.hint.answerImageUrlList,
                                            subscribeStatus = state.userSubscribeStatus,
                                            networkDisconnectedCount = state.networkDisconnectedCount,
                                            onImageClick = onAnswerImageClick,
                                            modifier = Modifier.padding(bottom = 20.dp)
                                        )
                                    }

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
                                                modifier = modifier.size(20.dp),
                                                painter = painterResource(R.drawable.ic_lock),
                                                colorFilter = ColorFilter.tint(NRColor.White),
                                                contentDescription = null,
                                            )
                                            Text(
                                                text = stringResource(R.string.text_open_answer_guide_message),
                                                color = NRColor.White,
                                                style = NRTypo.Pretendard.size14SemiBold,
                                                modifier = modifier
                                                    .padding(top = 10.dp)
                                                    .throttleClick { onAnswerOpenClick() }
                                            )
                                            Text(
                                                text = stringResource(R.string.game_view_answer),
                                                color = NRColor.Black,
                                                style = NRTypo.Pretendard.size16Bold,
                                                modifier = modifier
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

        NRLoading(isVisible = state.loading)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun HintScreenWithNoImagesPreview() {
    HintScreen(
        state = HintState(
            loading = false,
            hint = Hint(
                id = 1,
                progress = 45,
                hint = "이 힌트는 미로 출구를 찾는 데 도움이 됩니다. 벽의 패턴을 주의 깊게 살펴보세요.",
                answer = "미로의 출구는 북쪽 벽의 세 번째 문입니다. 빨간색 표시를 따라가세요.",
                hintImageUrlList = emptyList(),
                answerImageUrlList = emptyList()
            ),
            userSubscribeStatus = SubscribeStatus.Subscribed,
            networkDisconnectedCount = 0,
            isHintOpened = true
        ),
        onHintImageClick = {},
        onAnswerImageClick = {},
        onHintOpenClick = {},
        onAnswerOpenClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun HintScreenWithImagesPreview() {
    HintScreen(
        state = HintState(
            loading = false,
            hint = Hint(
                id = 1,
                progress = 45,
                hint = "이 힌트는 미로 출구를 찾는 데 도움이 됩니다. 벽의 패턴을 주의 깊게 살펴보세요.",
                answer = "미로의 출구는 북쪽 벽의 세 번째 문입니다. 빨간색 표시를 따라가세요.",
                hintImageUrlList = listOf(
                    "https://example.com/hint1.jpg",
                    "https://example.com/hint2.jpg"
                ),
                answerImageUrlList = emptyList()
            ),
            userSubscribeStatus = SubscribeStatus.Subscribed,
            networkDisconnectedCount = 0,
            isHintOpened = false
        ),
        onHintImageClick = {},
        onAnswerImageClick = {},
        onHintOpenClick = {},
        onAnswerOpenClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun HintScreenAnswerOpenedPreview() {
    HintScreen(
        state = HintState(
            loading = false,
            hint = Hint(
                id = 1,
                progress = 85,
                hint = "이 힌트는 미로 출구를 찾는 데 도움이 됩니다. 벽의 패턴을 주의 깊게 살펴보세요.",
                answer = "미로의 출구는 북쪽 벽의 세 번째 문입니다. 빨간색 표시를 따라가세요.",
                hintImageUrlList = emptyList(),
                answerImageUrlList = emptyList(),
            ),
            userSubscribeStatus = SubscribeStatus.Subscribed,
            networkDisconnectedCount = 0,
            isHintOpened = true,
            isAnswerOpened = true
        ),
        onHintImageClick = {},
        onAnswerImageClick = {},
        onHintOpenClick = {},
        onAnswerOpenClick = {}
    )
}

@Composable
fun HintTimerToolbar(
    lastSecondsFlow: kotlinx.coroutines.flow.Flow<Int>,
    onBackClick: () -> Unit,
    onMemoClick: () -> Unit
) {
    val lastSeconds by lastSecondsFlow.collectAsState(initial = 0)

    val timerText = remember(lastSeconds) {
        lastSeconds.toTimerFormat()
    }

    NRToolbar(
        title = timerText,
        onBackClick = onBackClick,
        onRightButtonClick = onMemoClick
    )
}
