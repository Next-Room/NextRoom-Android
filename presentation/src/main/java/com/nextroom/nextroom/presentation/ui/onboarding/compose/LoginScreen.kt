package com.nextroom.nextroom.presentation.ui.onboarding.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRLoading
import com.nextroom.nextroom.presentation.common.compose.NRTypo

@Composable
fun LoginScreen(
    isLoading: Boolean,
    onGoogleLoginClick: () -> Unit,
    onEmailLoginClick: () -> Unit,
    onTryWithoutLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NRColor.Dark01),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(92.dp))
            Text(
                text = stringResource(R.string.onboarding_description),
                style = NRTypo.Pretendard.size20,
                color = NRColor.Gray01,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.onboarding_title),
                style = NRTypo.Pretendard.size32Bold,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = painterResource(R.drawable.bg_onboarding),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )
        }

        Image(
            painter = painterResource(R.drawable.bg_black_to_white_gradient),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoginGuideLabel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            GoogleLoginButton(
                onClick = onGoogleLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            EmailLoginButton(
                onClick = onEmailLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.text_try_without_login),
                style = NRTypo.Pretendard.size14,
                color = NRColor.Gray01,
                modifier = Modifier
                    .clickable(onClick = onTryWithoutLoginClick)
                    .padding(8.dp),
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        NRLoading(isVisible = isLoading)
    }
}

@Composable
private fun LoginGuideLabel(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = NRColor.White12,
        )
        Text(
            text = stringResource(R.string.text_recommend_google_login_guide),
            style = NRTypo.Pretendard.size14,
            color = NRColor.White,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = NRColor.White12,
        )
    }
}

@Composable
private fun GoogleLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(NRColor.White)
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.text_start_with_google),
            style = NRTypo.Pretendard.size16Bold,
            color = NRColor.Black,
        )
    }
}

@Composable
private fun EmailLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(NRColor.Black)
            .border(
                width = 1.dp,
                color = NRColor.White20,
                shape = RoundedCornerShape(100.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.text_start_with_email),
            style = NRTypo.Pretendard.size16Bold,
            color = NRColor.White,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516, heightDp = 800)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        isLoading = false,
        onGoogleLoginClick = {},
        onEmailLoginClick = {},
        onTryWithoutLoginClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516, heightDp = 800)
@Composable
private fun LoginScreenLoadingPreview() {
    LoginScreen(
        isLoading = true,
        onGoogleLoginClick = {},
        onEmailLoginClick = {},
        onTryWithoutLoginClick = {},
    )
}
