package com.nextroom.nextroom.presentation.ui.login.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRLoading
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.ui.login.EmailLoginViewModel

@Composable
fun EmailLoginScreen(
    state: EmailLoginViewModel.UiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onEmailSaveCheckedChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    onCustomerServiceClick: () -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NRColor.Dark01),
    ) {
        Image(
            painter = painterResource(R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .alpha(0.4f),
            contentScale = ContentScale.FillWidth,
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(44.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                LoginTextField(
                    value = state.currentEmailInput,
                    onValueChange = onEmailChange,
                    hint = stringResource(R.string.login_admin_email_hint),
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    isError = state.hasError,
                    enabled = !state.loading,
                )
                Spacer(modifier = Modifier.height(16.dp))
                LoginTextField(
                    value = state.currentPasswordInput,
                    onValueChange = onPasswordChange,
                    hint = stringResource(R.string.login_password_hint),
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    isPassword = true,
                    onImeAction = onLoginClick,
                    isError = state.hasError,
                    enabled = !state.loading,
                )
                Spacer(modifier = Modifier.height(16.dp))
                EmailSaveCheckBox(
                    checked = state.emailSaveChecked,
                    onCheckedChange = onEmailSaveCheckedChange,
                )
                Spacer(modifier = Modifier.height(40.dp))
                EmailLoginButton(
                    enabled = !state.loading,
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
                GoogleLoginButton(
                    onClick = onGoogleLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(4.dp))
                BottomLinks(
                    onCustomerServiceClick = onCustomerServiceClick,
                    onSignupClick = onSignupClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        NRLoading(isVisible = state.loading)
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = false),
                    onClick = onBackClick,
                )
                .padding(20.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_navigate_back_24),
                contentDescription = stringResource(R.string.toolbar_navigate_back_description),
                tint = NRColor.White,
            )
        }
        Text(
            text = stringResource(R.string.text_email_login),
            style = NRTypo.Pretendard.size20,
            color = NRColor.White,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    isError: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    onImeAction: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val underlineColor = when {
        isError -> NRColor.Red
        isFocused || value.isNotEmpty() -> NRColor.White
        else -> NRColor.Gray01
    }
    val textColor = if (isFocused || value.isNotEmpty()) NRColor.White else NRColor.Gray01

    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            textStyle = NRTypo.Pretendard.size16.copy(color = textColor),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(NRColor.White),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeAction() },
                onGo = { onImeAction() },
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        style = NRTypo.Pretendard.size16,
                        color = NRColor.Gray01,
                    )
                }
                innerTextField()
            },
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(underlineColor),
        )
    }
}

@Composable
private fun EmailSaveCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onCheckedChange(!checked) },
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = NRColor.White,
                uncheckedColor = NRColor.Gray01,
                checkmarkColor = NRColor.Black,
            ),
        )
        Text(
            text = stringResource(R.string.email_save_title),
            style = NRTypo.Pretendard.size14,
            color = NRColor.Gray01,
        )
    }
}

@Composable
private fun EmailLoginButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(NRColor.White)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.text_login),
            style = NRTypo.Pretendard.size16Bold,
            color = NRColor.Black,
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
            .background(NRColor.Black)
            .border(
                width = 1.dp,
                color = NRColor.White20,
                shape = RoundedCornerShape(100.dp),
            )
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
            color = NRColor.White,
        )
    }
}

@Composable
private fun BottomLinks(
    onCustomerServiceClick: () -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.text_customer_service),
            style = NRTypo.Pretendard.size12,
            color = NRColor.Gray01,
            modifier = Modifier
                .clickable(onClick = onCustomerServiceClick)
                .padding(16.dp),
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(12.dp)
                .background(NRColor.Gray02),
        )
        Text(
            text = stringResource(R.string.sign_up),
            style = NRTypo.Pretendard.size12,
            color = NRColor.Gray01,
            modifier = Modifier
                .clickable(onClick = onSignupClick)
                .padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516, heightDp = 800)
@Composable
private fun EmailLoginScreenPreview() {
    EmailLoginScreen(
        state = EmailLoginViewModel.UiState(),
        onEmailChange = {},
        onPasswordChange = {},
        onEmailSaveCheckedChange = {},
        onLoginClick = {},
        onGoogleLoginClick = {},
        onBackClick = {},
        onCustomerServiceClick = {},
        onSignupClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516, heightDp = 800)
@Composable
private fun EmailLoginScreenFilledPreview() {
    EmailLoginScreen(
        state = EmailLoginViewModel.UiState(
            currentEmailInput = "test@nextroom.co.kr",
            currentPasswordInput = "password",
            emailSaveChecked = true,
            hasError = true,
        ),
        onEmailChange = {},
        onPasswordChange = {},
        onEmailSaveCheckedChange = {},
        onLoginClick = {},
        onGoogleLoginClick = {},
        onBackClick = {},
        onCustomerServiceClick = {},
        onSignupClick = {},
    )
}
