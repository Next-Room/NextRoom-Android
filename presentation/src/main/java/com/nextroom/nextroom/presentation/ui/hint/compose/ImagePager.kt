package com.nextroom.nextroom.presentation.ui.hint.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.extension.throttleClick

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePager(
    imageUrls: List<String>,
    subscribeStatus: SubscribeStatus,
    networkDisconnectedCount: Int,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (imageUrls.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
        ) { page ->
            val imageModel = when {
                subscribeStatus == SubscribeStatus.SUBSCRIPTION_EXPIRATION
                        || subscribeStatus == SubscribeStatus.Default ->
                    R.drawable.img_error

                networkDisconnectedCount > SUBSCRIBE_CHECK_LIMIT ->
                    R.drawable.img_error

                subscribeStatus == SubscribeStatus.Subscribed ->
                    imageUrls[page]

                else -> null
            }

            AsyncImage(
                model = imageModel,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .throttleClick {
                        onImageClick(page)
                    }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        PagerIndicator(
            pageCount = imageUrls.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            val color = if (index == currentPage) {
                NRColor.White
            } else {
                NRColor.Gray05
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .size(8.dp)
                    .background(color, CircleShape)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun ImagePagerPreview() {
    ImagePager(
        imageUrls = listOf(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg",
            "https://example.com/image3.jpg"
        ),
        subscribeStatus = SubscribeStatus.Subscribed,
        networkDisconnectedCount = 0,
        onImageClick = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun ImagePagerExpiredPreview() {
    ImagePager(
        imageUrls = listOf(
            "https://example.com/image1.jpg"
        ),
        subscribeStatus = SubscribeStatus.SUBSCRIPTION_EXPIRATION,
        networkDisconnectedCount = 0,
        onImageClick = {}
    )
}

private const val SUBSCRIBE_CHECK_LIMIT = 3