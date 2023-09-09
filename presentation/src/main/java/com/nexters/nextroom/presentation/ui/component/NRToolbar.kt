package com.nexters.nextroom.presentation.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.nextroom.presentation.ui.component.button.SubButton
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import com.nexters.nextroom.presentation.ui.theme.ToolbarTimerTitle

interface ToolbarAction {
    val label: String
    fun onAction()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NRToolbar(
    modifier: Modifier = Modifier,
    title: String,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    showBackButton: Boolean = false,
    onBackClicked: () -> Unit = {},
    action: ToolbarAction?,
) {
    CenterAlignedTopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = { Text(text = title, fontSize = 20.sp, style = MaterialTheme.typography.ToolbarTimerTitle) },
        windowInsets = WindowInsets(left = 20.dp, right = 20.dp),
        navigationIcon = {
            if (showBackButton) {
                Icon(
                    modifier = Modifier.clickable { onBackClicked() },
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로 가기",
                )
            }
        },
        actions = {
            action?.let {
                SubButton(text = action.label, isKorean = false) {
                    action.onAction()
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = backgroundColor),
    )
}

@Preview
@Composable
fun NRToolbarPreView() {
    NextRoomTheme {
        Surface {
            NRToolbar(
                title = "1:00:00",
                showBackButton = true,
                action = object : ToolbarAction {
                    override val label: String = "MEMO"
                    override fun onAction() {}
                },
            )
        }
    }
}
