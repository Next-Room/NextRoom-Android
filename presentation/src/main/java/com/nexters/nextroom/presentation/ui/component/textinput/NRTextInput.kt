package com.nexters.nextroom.presentation.ui.component.textinput

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.nextroom.presentation.ui.theme.Gray01
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import com.nexters.nextroom.presentation.ui.theme.Red
import com.nexters.nextroom.presentation.ui.theme.TextInput
import com.nexters.nextroom.presentation.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NRTextInput(
    modifier: Modifier = Modifier,
    value: String,
    hint: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onValueChange: (String) -> Unit,
) {
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedIndicatorColor = White,
        unfocusedIndicatorColor = Gray01,
        errorIndicatorColor = Red,
        unfocusedTextColor = White,
        focusedTextColor = White,
        errorTextColor = White,
    )
    val visualTransformation = if (isPassword) {
        PasswordVisualTransformation('●')
    } else {
        VisualTransformation.None
    }

    val textStyle = MaterialTheme.typography.TextInput
    val textColor = textStyle.color.takeOrElse {
        when {
            isError -> Red
            !enabled -> Gray01
            else -> White
        }
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    BasicTextField(
        value = value,
        modifier = modifier.wrapContentHeight(),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = false,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(White),
        visualTransformation = visualTransformation,
        singleLine = true,
        interactionSource = interactionSource,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                enabled = enabled,
                isError = isError,
                singleLine = true,
                contentPadding = PaddingValues(start = 4.dp, end = 4.dp, bottom = 8.dp),
                interactionSource = interactionSource,
                colors = colors,
                placeholder = {
                    hint?.let {
                        Text(
                            text = hint,
                            color = Gray01,
                            fontSize = 16.sp,
                        )
                    }
                },
            )
        },
    )
}

@Preview
@Composable
fun NRTextInputPreview() {
    NextRoomTheme {
        Surface(Modifier.fillMaxSize()) {
            var text by remember { mutableStateOf("error") }
            NRTextInput(
                value = text,
                onValueChange = { text = it },
                isError = text == "error",
                isPassword = false,
            )
        }
    }
}
