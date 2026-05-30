package com.nextroom.nextroom.presentation.ui.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRToolbar
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.extension.throttleClick

@Composable
fun MypageScreen(
    state: MypageViewModel.UiState.Loaded,
    onBackClick: () -> Unit,
    onSubscribeClick: () -> Unit,
    onChangeAppPasswordClick: () -> Unit,
    onCustomerServiceClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onResignClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NRColor.Dark01),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NRToolbar(
                title = stringResource(R.string.mypage_title),
                onBackClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = stringResource(R.string.admin_main_shop_name_label),
                style = NRTypo.Pretendard.size14SemiBold,
                color = NRColor.White,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp),
            )

            Text(
                text = state.shopName,
                style = NRTypo.Pretendard.size24,
                color = NRColor.White,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(1.dp)
                    .background(NRColor.Gray02),
            )

            Spacer(modifier = Modifier.height(16.dp))

            MypageMenuRow(
                title = stringResource(R.string.subscribe),
                onClick = onSubscribeClick,
            )

            MypageMenuRow(
                title = stringResource(R.string.text_change_app_password),
                onClick = onChangeAppPasswordClick,
            )

            MypageMenuRow(
                title = stringResource(R.string.text_customer_service),
                onClick = onCustomerServiceClick,
            )

            MypageAppVersionRow(appVersion = state.appVersion)

            Spacer(modifier = Modifier.weight(1f))

            MypageBottomActions(
                onLogoutClick = onLogoutClick,
                onResignClick = onResignClick,
            )
        }
    }
}

@Composable
private fun MypageMenuRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .throttleClick { onClick() }
            .padding(horizontal = 20.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = NRTypo.Pretendard.size16Bold,
            color = NRColor.White,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(R.drawable.ic_navigate_next),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun MypageAppVersionRow(
    appVersion: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.text_app_version),
            style = NRTypo.Pretendard.size16Bold,
            color = NRColor.White,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = appVersion,
            style = NRTypo.Pretendard.size16,
            color = NRColor.White50,
        )
    }
}

@Composable
private fun MypageBottomActions(
    onLogoutClick: () -> Unit,
    onResignClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.logout_button),
            style = NRTypo.Pretendard.size14,
            color = NRColor.White50,
            modifier = Modifier
                .throttleClick { onLogoutClick() }
                .padding(16.dp),
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(10.dp)
                .background(NRColor.White50),
        )
        Text(
            text = stringResource(R.string.text_user_resign),
            style = NRTypo.Pretendard.size14,
            color = NRColor.White50,
            modifier = Modifier
                .throttleClick { onResignClick() }
                .padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516)
@Composable
private fun MypageScreenPreview() {
    MypageScreen(
        state = MypageViewModel.UiState.Loaded(
            shopName = "비트포비아 강남 2호점",
            status = SubscribeStatus.Subscribed,
            appVersion = "1.4.7",
        ),
        onBackClick = {},
        onSubscribeClick = {},
        onChangeAppPasswordClick = {},
        onCustomerServiceClick = {},
        onLogoutClick = {},
        onResignClick = {},
    )
}
