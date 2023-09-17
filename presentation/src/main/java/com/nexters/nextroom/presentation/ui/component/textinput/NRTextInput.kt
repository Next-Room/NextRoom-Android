package com.nexters.nextroom.presentation.ui.component.textinput

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import com.nexters.nextroom.presentation.ui.theme.Gray01
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import com.nexters.nextroom.presentation.ui.theme.Red
import com.nexters.nextroom.presentation.ui.theme.TextInput
import com.nexters.nextroom.presentation.ui.theme.White

@Composable
fun NRTextInput(
    modifier: Modifier = Modifier,
    value: String,
    hint: String? = null,
    isError: Boolean = false,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onValueChange: (String) -> Unit,
) {
    TextField(
        modifier = modifier.wrapContentHeight(),
        value = value,
        onValueChange = onValueChange,
        placeholder = hint?.let {
            {
                Text(
                    text = hint,
                    color = Gray01,
                    fontSize = 16.sp,
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = White,
            unfocusedIndicatorColor = Gray01,
            errorIndicatorColor = Red,
            unfocusedTextColor = Gray01,
            focusedTextColor = White,
            errorTextColor = White,
        ),
        textStyle = MaterialTheme.typography.TextInput,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation('●')
        } else {
            VisualTransformation.None
        },
        singleLine = true,
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NRTextInputPreview() {
    NextRoomTheme {
        Surface(Modifier.fillMaxSize()) {
            var text by remember { mutableStateOf("error") }
            NRTextInput(
                value = text,
                onValueChange = { text = it },
                isError = text == "error",
                isPassword = true,
            )
        }
    }
}
