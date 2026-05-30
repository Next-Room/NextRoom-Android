package com.nextroom.nextroom.presentation.ui.login.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRLoading
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.ui.login.SignupViewModel

@Composable
fun SignupScreen(
    state: SignupViewModel.UIState,
    onBackClick: () -> Unit,
    onShopNameChange: (String) -> Unit,
    onSignupSourceClick: () -> Unit,
    onCustomSignupSourceChange: (String) -> Unit,
    onSignupReasonClick: () -> Unit,
    onCustomSignupReasonChange: (String) -> Unit,
    onAllTermsAgreeClick: (Boolean) -> Unit,
    onServiceTermAgreeClick: (Boolean) -> Unit,
    onMarketingTermAgreeClick: (Boolean) -> Unit,
    onServiceTermLinkClick: () -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val loaded = state as? SignupViewModel.UIState.Loaded
    val isLoading = state is SignupViewModel.UIState.Loading
    val etcText = stringResource(R.string.text_etc)
    val isCustomSourceVisible = loaded?.selectedSignupSource?.text == etcText
    val isCustomReasonVisible = loaded?.selectedSignupReason?.text == etcText

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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 60.dp,
                            bottom = 140.dp,
                        )
                    ),
            ) {
                LabelWithAsterisk(text = stringResource(R.string.text_shop_name))
                Spacer(modifier = Modifier.height(12.dp))
                SignupInputField(
                    value = loaded?.shopName.orEmpty(),
                    onValueChange = onShopNameChange,
                    hint = stringResource(R.string.text_please_input),
                )

                Spacer(modifier = Modifier.height(16.dp))
                LabelWithAsterisk(text = stringResource(R.string.text_singup_source))
                Spacer(modifier = Modifier.height(12.dp))
                SelectField(
                    text = loaded?.selectedSignupSource?.text,
                    onClick = onSignupSourceClick,
                )
                if (isCustomSourceVisible) {
                    Spacer(modifier = Modifier.height(12.dp))
                    SignupInputField(
                        value = loaded?.customSignupSource.orEmpty(),
                        onValueChange = onCustomSignupSourceChange,
                        hint = stringResource(R.string.text_please_input),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.text_singup_reason),
                    style = NRTypo.Pretendard.size16,
                    color = NRColor.White70,
                )
                Spacer(modifier = Modifier.height(12.dp))
                SelectField(
                    text = loaded?.selectedSignupReason?.text,
                    onClick = onSignupReasonClick,
                )
                if (isCustomReasonVisible) {
                    Spacer(modifier = Modifier.height(12.dp))
                    SignupInputField(
                        value = loaded?.customSignupReason.orEmpty(),
                        onValueChange = onCustomSignupReasonChange,
                        hint = stringResource(R.string.text_please_input),
                    )
                }

                Spacer(modifier = Modifier.height(52.dp))
                AgreeAllTermsRow(
                    checked = loaded?.allTermsAgreed == true,
                    onClick = { onAllTermsAgreeClick(loaded?.allTermsAgreed != true) },
                )

                Spacer(modifier = Modifier.height(8.dp))
                ServiceTermAgreeRow(
                    checked = loaded?.serviceTermAgreed == true,
                    onRowClick = { onServiceTermAgreeClick(loaded?.serviceTermAgreed != true) },
                    onLinkClick = onServiceTermLinkClick,
                )

                MarketingTermAgreeRow(
                    checked = loaded?.marketingTermAgreed == true,
                    onClick = { onMarketingTermAgreeClick(loaded?.marketingTermAgreed != true) },
                )
            }

            SignupCompleteButton(
                enabled = loaded?.allRequiredFieldFilled == true,
                onClick = onSignupClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 50.dp),
            )
        }

        NRLoading(isVisible = isLoading)
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
            text = stringResource(R.string.sign_up),
            style = NRTypo.Pretendard.size20,
            color = NRColor.White,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun LabelWithAsterisk(text: String) {
    Row {
        Text(
            text = text,
            style = NRTypo.Pretendard.size16,
            color = NRColor.White70,
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = stringResource(R.string.text_asterisk),
            style = NRTypo.Pretendard.size16,
            color = NRColor.Red02,
        )
    }
}

@Composable
private fun SignupInputField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = if (isFocused) NRColor.White50 else NRColor.White20

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(NRColor.Black)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = NRTypo.Pretendard.size16.copy(color = NRColor.White),
            cursorBrush = SolidColor(NRColor.White),
            interactionSource = interactionSource,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() },
            ),
            modifier = Modifier.fillMaxWidth(),
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
    }
}

@Composable
private fun SelectField(
    text: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(NRColor.Black)
            .border(
                width = 1.dp,
                color = NRColor.White20,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text ?: stringResource(R.string.text_please_select),
            style = NRTypo.Pretendard.size16,
            color = if (text == null) NRColor.Gray01 else NRColor.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
        )
        Image(
            painter = painterResource(R.drawable.ic_arrow_down),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun AgreeAllTermsRow(
    checked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(NRColor.White12)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.text_agree_all_terms),
            style = NRTypo.Pretendard.size14,
            color = NRColor.White,
            modifier = Modifier.weight(1f),
        )
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = signupCheckboxColors(),
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun ServiceTermAgreeRow(
    checked: Boolean,
    onRowClick: () -> Unit,
    onLinkClick: () -> Unit,
) {
    val linkPortion = stringResource(R.string.text_service_term_agree_link)
    val suffix = stringResource(R.string.text_service_term_agree_suffix)
    val annotated = buildAnnotatedString {
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append(linkPortion)
        }
        append(suffix)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRowClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = annotated,
            style = NRTypo.Pretendard.size12,
            color = NRColor.White,
            modifier = Modifier.clickable(onClick = onLinkClick),
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = stringResource(R.string.text_required_label),
            style = NRTypo.Pretendard.size12,
            color = NRColor.Red02,
            modifier = Modifier.weight(1f),
        )
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = signupCheckboxColors(),
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun MarketingTermAgreeRow(
    checked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.text_marketing_term_agree),
            style = NRTypo.Pretendard.size12,
            color = NRColor.White,
            modifier = Modifier.weight(1f),
        )
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = signupCheckboxColors(),
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun SignupCompleteButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        if (enabled) NRColor.PrimaryButtonBackground else NRColor.DisabledButtonBackground
    val textColor = if (enabled) NRColor.PrimaryButtonText else NRColor.DisabledButtonText

    Box(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.text_signup_complete),
            style = NRTypo.Pretendard.size16Bold,
            color = textColor,
        )
    }
}

@Composable
private fun signupCheckboxColors() = CheckboxDefaults.colors(
    checkedColor = NRColor.White,
    uncheckedColor = NRColor.Gray01,
    checkmarkColor = NRColor.Black,
)

@Composable
fun rememberSignupSourceItems(): List<String> =
    stringArrayResource(R.array.signup_source).toList()

@Composable
fun rememberSignupReasonItems(): List<String> =
    stringArrayResource(R.array.signup_reason).toList()

@Preview(showBackground = true, backgroundColor = 0xFF151516, heightDp = 900)
@Composable
private fun SignupScreenEmptyPreview() {
    SignupScreen(
        state = SignupViewModel.UIState.Loaded(
            shopName = null,
            selectedSignupSource = null,
            selectedSignupReason = null,
            customSignupSource = null,
            customSignupReason = null,
            serviceTermAgreed = false,
            marketingTermAgreed = false,
            allTermsAgreed = false,
            allRequiredFieldFilled = false,
        ),
        onBackClick = {},
        onShopNameChange = {},
        onSignupSourceClick = {},
        onCustomSignupSourceChange = {},
        onSignupReasonClick = {},
        onCustomSignupReasonChange = {},
        onAllTermsAgreeClick = {},
        onServiceTermAgreeClick = {},
        onMarketingTermAgreeClick = {},
        onServiceTermLinkClick = {},
        onSignupClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516, heightDp = 900)
@Composable
private fun SignupScreenFilledPreview() {
    SignupScreen(
        state = SignupViewModel.UIState.Loaded(
            shopName = "NextRoom 강남점",
            selectedSignupSource = SignupViewModel.UIState.Loaded.SelectedItem(
                id = "5",
                text = "기타",
            ),
            selectedSignupReason = SignupViewModel.UIState.Loaded.SelectedItem(
                id = "0",
                text = "운영 중인 매장에 도입하기 위해",
            ),
            customSignupSource = "지하철 광고",
            customSignupReason = null,
            serviceTermAgreed = true,
            marketingTermAgreed = true,
            allTermsAgreed = true,
            allRequiredFieldFilled = true,
        ),
        onBackClick = {},
        onShopNameChange = {},
        onSignupSourceClick = {},
        onCustomSignupSourceChange = {},
        onSignupReasonClick = {},
        onCustomSignupReasonChange = {},
        onAllTermsAgreeClick = {},
        onServiceTermAgreeClick = {},
        onMarketingTermAgreeClick = {},
        onServiceTermLinkClick = {},
        onSignupClick = {},
    )
}
