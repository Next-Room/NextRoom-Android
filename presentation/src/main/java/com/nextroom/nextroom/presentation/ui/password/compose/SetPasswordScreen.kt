package com.nextroom.nextroom.presentation.ui.password.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRTypo
import com.nextroom.nextroom.presentation.model.InputState
import com.nextroom.nextroom.presentation.ui.password.SetPasswordViewModel
import com.nextroom.nextroom.presentation.ui.tutorial.timer.compose.CodeInputSection

@Composable
fun SetPasswordScreen(
    state: SetPasswordViewModel.UiState,
    onKeyClick: (Int) -> Unit,
    onBackspaceClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val headerRes = when (state.step) {
        SetPasswordViewModel.UiState.Step.PasswordSetting -> R.string.text_set_password
        SetPasswordViewModel.UiState.Step.PasswordConfirm -> R.string.text_confirm_password
    }
    val descriptionRes = when (state.step) {
        SetPasswordViewModel.UiState.Step.PasswordSetting -> R.string.text_set_password_description
        SetPasswordViewModel.UiState.Step.PasswordConfirm -> R.string.text_set_password_description_for_confirm
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NRColor.Dark01)
    ) {
        BackIcon(onClick = onBackClick)

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(headerRes),
                    style = NRTypo.Poppins.size24,
                    color = NRColor.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(descriptionRes),
                    style = NRTypo.Pretendard.size16Bold,
                    color = NRColor.Gray01
                )
                Spacer(modifier = Modifier.height(24.dp))
                CodeInputSection(
                    code = state.displayPassword,
                    inputState = state.inputState
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(28.dp))
                PinKeypad(
                    onKeyClick = onKeyClick,
                    onBackspaceClick = onBackspaceClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(42.dp))
            }
        }
    }
}

@Composable
private fun BackIcon(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(64.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false)
            ) { onClick() }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_navigate_back_24),
            contentDescription = stringResource(R.string.toolbar_navigate_back_description),
            tint = NRColor.White
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF151516, heightDp = 720)
@Composable
private fun SetPasswordSettingPreview() {
    SetPasswordScreen(
        state = SetPasswordViewModel.UiState(
            displayPassword = "12",
            step = SetPasswordViewModel.UiState.Step.PasswordSetting,
            inputState = InputState.Typing,
        ),
        onKeyClick = {},
        onBackspaceClick = {},
        onBackClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF151516, heightDp = 720)
@Composable
private fun SetPasswordConfirmPreview() {
    SetPasswordScreen(
        state = SetPasswordViewModel.UiState(
            displayPassword = "",
            step = SetPasswordViewModel.UiState.Step.PasswordConfirm,
            inputState = InputState.Error(R.string.text_incorrect_password_error_message),
        ),
        onKeyClick = {},
        onBackspaceClick = {},
        onBackClick = {},
    )
}
