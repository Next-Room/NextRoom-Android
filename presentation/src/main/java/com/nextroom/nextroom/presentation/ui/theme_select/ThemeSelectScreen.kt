package com.nextroom.nextroom.presentation.ui.theme_select

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nextroom.nextroom.domain.model.Banner
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRLoading
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ThemeSelectScreen(
    state: ThemeSelectUiState,
    onRefresh: () -> Unit,
    onBannerClick: (Banner) -> Unit,
    onPurchaseClick: () -> Unit,
    onMyPageClick: () -> Unit,
    onManageThemesClick: () -> Unit,
    onBackgroundSettingClick: () -> Unit,
    onThemeRefreshClick: () -> Unit,
    onThemeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullToRefreshState = rememberPullToRefreshState()

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }

    LaunchedEffect(state.loading) {
        if (!state.loading) {
            pullToRefreshState.endRefresh()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NRColor.Dark01)
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            ThemeSelectHeader(
                shopName = state.shopName,
                subscribeStatus = state.subscribeStatus,
                onPurchaseClick = onPurchaseClick,
                onMyPageClick = onMyPageClick,
            )

            if (state.banners.isNotEmpty()) {
                BannerSection(
                    banners = state.banners,
                    onBannerClick = onBannerClick,
                )
            }

            ThemeSectionHeader(
                themeCount = state.themes.size,
                recentUpdatedDate = state.recentUpdatedDate,
                onManageThemesClick = onManageThemesClick,
                onBackgroundSettingClick = onBackgroundSettingClick,
                onThemeRefreshClick = onThemeRefreshClick,
            )

            if (state.themes.isEmpty() && !state.loading) {
                EmptyThemeGuide()
            } else {
                state.themes.forEach { theme ->
                    ThemeItem(
                        theme = theme,
                        onThemeClick = { onThemeClick(theme.id) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            containerColor = NRColor.Sub1,
            contentColor = NRColor.White,
        )

        NRLoading(isVisible = state.opaqueLoading || (state.loading && state.themes.isEmpty()))
    }
}

@Composable
private fun ThemeSelectHeader(
    shopName: String,
    subscribeStatus: SubscribeStatus,
    onPurchaseClick: () -> Unit,
    onMyPageClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(end = 20.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (subscribeStatus != SubscribeStatus.Subscribed) {
                Text(
                    text = stringResource(R.string.purchase_ticket),
                    style = NRTypo.Pretendard.size14SemiBold,
                    color = NRColor.White,
                    modifier = Modifier
                        .border(1.dp, NRColor.Gray01, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onPurchaseClick)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            IconButton(
                onClick = onMyPageClick,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_my),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, top = 32.dp + 40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_locate),
                contentDescription = null,
                tint = NRColor.White70,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = shopName,
                style = NRTypo.Pretendard.size20SemiBold,
                color = NRColor.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BannerSection(
    banners: List<Banner>,
    onBannerClick: (Banner) -> Unit,
    autoScrollDuration: Long = 3500L,
) {
    if (banners.isEmpty()) return

    val pageCount = banners.size * 1000
    val startIndex = pageCount / 2
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { pageCount }
    )

    LaunchedEffect(pagerState, banners.size) {
        while (pagerState.currentPage < pageCount - 1) {
            delay(autoScrollDuration)
            if (!pagerState.isScrollInProgress) {
                val next = pagerState.currentPage + 1
                pagerState.animateScrollToPage(next)
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .height(84.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        pageSize = PageSize.Fill,
        pageSpacing = 8.dp,
        key = { it },
    ) { page ->
        val realIndex = page % banners.size
        val banner = banners[realIndex]

        AsyncImage(
            model = banner.imageUrl,
            contentDescription = banner.description,
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.img_banner_error),
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onBannerClick(banner) },
        )
    }
}

@Composable
private fun ThemeSectionHeader(
    themeCount: Int,
    recentUpdatedDate: Long?,
    onManageThemesClick: () -> Unit,
    onBackgroundSettingClick: () -> Unit,
    onThemeRefreshClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 43.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.tv_theme),
                style = NRTypo.Pretendard.size18SemiBold,
                color = NRColor.White,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = themeCount.toString(),
                style = NRTypo.Pretendard.size16SemiBold,
                color = NRColor.White50,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.text_manage_themes),
                style = NRTypo.Pretendard.size14,
                color = NRColor.White70,
                modifier = Modifier
                    .clickable(onClick = onManageThemesClick)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            )
            Text(
                text = stringResource(R.string.text_background_setting),
                style = NRTypo.Pretendard.size14,
                color = NRColor.White70,
                modifier = Modifier
                    .clickable(onClick = onBackgroundSettingClick)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val updateText = if (recentUpdatedDate == null) {
                stringResource(R.string.text_last_hint_update_fail)
            } else {
                stringResource(
                    R.string.text_last_hint_update,
                    formatDateLong(recentUpdatedDate)
                )
            }
            Text(
                text = updateText,
                style = NRTypo.Pretendard.size14,
                color = NRColor.White50,
            )
            Spacer(modifier = Modifier.width(7.dp))
            IconButton(
                onClick = onThemeRefreshClick,
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_refrash),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }
        }
    }
}

@Composable
private fun formatDateLong(timestamp: Long): String {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy.MM.dd HH:mm", java.util.Locale.getDefault())
        sdf.format(java.util.Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}

@Composable
private fun EmptyThemeGuide() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp, bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_empty),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(120.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.admin_main_empty_theme_guide),
            style = NRTypo.Pretendard.size20,
            color = NRColor.White,
        )
    }
}

@Composable
private fun ThemeItem(
    theme: ThemeInfoPresentation,
    onThemeClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(NRColor.White5)
            .clickable(onClick = onThemeClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = theme.themeImageUrl,
            contentDescription = theme.title,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.img_placeholder),
            error = painterResource(R.drawable.img_placeholder),
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(width = 1.dp, color = NRColor.White12, shape = RoundedCornerShape(8.dp)),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = theme.title,
            style = NRTypo.Pretendard.size14,
            color = NRColor.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(20.dp))
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right_24),
            contentDescription = null,
            tint = NRColor.White,
            modifier = Modifier.size(16.dp),
        )
    }
}

// ==================== Previews ====================

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun ThemeSelectScreenLoadingPreview() {
    ThemeSelectScreen(
        state = ThemeSelectUiState(opaqueLoading = true),
        onRefresh = {},
        onBannerClick = {},

        onPurchaseClick = {},
        onMyPageClick = {},
        onManageThemesClick = {},
        onBackgroundSettingClick = {},
        onThemeRefreshClick = {},
        onThemeClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun ThemeSelectScreenEmptyPreview() {
    ThemeSelectScreen(
        state = ThemeSelectUiState(
            opaqueLoading = false,
            loading = false,
            shopName = "비트포비아 강남 2호점",
            themes = emptyList(),
        ),
        onRefresh = {},
        onBannerClick = {},

        onPurchaseClick = {},
        onMyPageClick = {},
        onManageThemesClick = {},
        onBackgroundSettingClick = {},
        onThemeRefreshClick = {},
        onThemeClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun ThemeSelectScreenWithDataPreview() {
    ThemeSelectScreen(
        state = ThemeSelectUiState(
            opaqueLoading = false,
            loading = false,
            shopName = "비트포비아 강남 2호점",
            themes = listOf(
                ThemeInfoPresentation(id = 1, title = "로스트 킹덤2 : 잃어버린 세계를 찾아서"),
                ThemeInfoPresentation(id = 2, title = "탈출 게임"),
            ),
            subscribeStatus = SubscribeStatus.Default,
            recentUpdatedDate = System.currentTimeMillis(),
        ),
        onRefresh = {},
        onBannerClick = {},

        onPurchaseClick = {},
        onMyPageClick = {},
        onManageThemesClick = {},
        onBackgroundSettingClick = {},
        onThemeRefreshClick = {},
        onThemeClick = {},
    )
}
