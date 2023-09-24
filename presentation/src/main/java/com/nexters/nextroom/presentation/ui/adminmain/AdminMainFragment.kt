package com.nexters.nextroom.presentation.ui.adminmain

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nexters.nextroom.presentation.R
import com.nexters.nextroom.presentation.extension.safeNavigate
import com.nexters.nextroom.presentation.model.ThemeInfoPresentation
import com.nexters.nextroom.presentation.ui.component.button.SubButton
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import com.nexters.nextroom.presentation.util.DateTimeUtil
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.compose.collectAsState

@AndroidEntryPoint
class AdminMainFragment : Fragment() {

    private lateinit var backCallback: OnBackPressedCallback

    private val viewModel: AdminMainViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                NextRoomTheme {
                    Surface {
                        val state by viewModel.collectAsState()
                        ThemesScreen(modifier = Modifier.fillMaxSize(), state = state)
                    }
                }
            }
        }
    }

    private fun startGame(code: Int) {
        viewModel.start(code) {
            val action = AdminMainFragmentDirections.actionAdminMainFragmentToVerifyFragment()
            findNavController().safeNavigate(action)
        }
    }

    override fun onDetach() {
        super.onDetach()
        backCallback.remove()
    }

    @Composable
    fun ThemesScreen(modifier: Modifier = Modifier, state: AdminMainState) {
        LazyColumn(modifier = modifier) {
            item {
                Header(state.showName) { viewModel.logout() }
            }
            itemsIndexed(items = state.themes, key = { _, theme -> theme.id }) { i, theme ->
                Column {
                    ThemeItem(theme = theme, onUpdate = viewModel::updateHints, onClick = ::startGame)
                    if (i != state.themes.lastIndex) {
                        Divider(
                            Modifier.fillMaxWidth(),
                            color = DividerDefaults.color.copy(alpha = 0.12f),
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun Header(shopName: String, onClickLogout: () -> Unit = {}) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                alpha = 0.3f,
                alignment = Alignment.BottomCenter,
                painter = painterResource(id = R.drawable.bg),
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
            )
            Column(Modifier.fillMaxWidth()) {
                val paddingHorizontal = 20.dp
                // 로그아웃 버튼
                SubButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .padding(top = 12.dp, end = paddingHorizontal),
                    text = stringResource(id = R.string.logout_button),
                    isKorean = false,
                ) {
                    onClickLogout()
                }
                Text(
                    text = stringResource(id = R.string.admin_main_shop_name_label),
                    modifier = Modifier.padding(top = 46.dp, start = paddingHorizontal, end = paddingHorizontal),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                )
                // 지점 이름
                Text(
                    text = shopName,
                    modifier = Modifier.padding(top = 4.dp, start = paddingHorizontal, end = paddingHorizontal),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = stringResource(id = R.string.admin_main_update_hint),
                    modifier = Modifier.padding(
                        top = 12.dp,
                        start = paddingHorizontal,
                        end = paddingHorizontal,
                        bottom = 38.dp,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
    }

    @Composable
    private fun ThemeItem(
        theme: ThemeInfoPresentation,
        onUpdate: (themeId: Int) -> Unit = {},
        onClick: (themeId: Int) -> Unit = {},
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val pressed by interactionSource.collectIsPressedAsState()
        Column(
            modifier = Modifier
                .background(
                    color = if (pressed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                )
                .padding(vertical = 28.dp, horizontal = 20.dp)
                .fillMaxWidth()
                .clickable(interactionSource = interactionSource, indication = null) {
                    onClick(theme.id)
                },
        ) {
            // 테마 이름
            Text(
                text = theme.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.size(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val updatedAt = if (theme.recentUpdated != 0L) {
                    DateTimeUtil().longToDateString(theme.recentUpdated, retPattern = "yyyy-MM-dd")
                } else {
                    stringResource(id = R.string.common_not_exists)
                }
                // 최근 업데이트 일
                Text(
                    text = stringResource(id = R.string.admin_main_updated_at, updatedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
                // 업데이트 버튼
                Image(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(20.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(color = MaterialTheme.colorScheme.onSurface)
                        .padding(2.5.dp)
                        .clickable { onUpdate(theme.id) },
                    painter = painterResource(id = R.drawable.ic_update24),
                    contentDescription = "${theme.title} 테마 업데이트 버튼",
                )
            }
        }
    }

    @Preview
    @Composable
    private fun HeaderPreview() {
        NextRoomTheme {
            Surface {
                Header("비트포비아 2호점")
            }
        }
    }

    @Preview
    @Composable
    fun ThemeItemPreview() {
        NextRoomTheme {
            Surface {
                ThemeItem(ThemeInfoPresentation(title = "메이데이", recentUpdated = 19123492))
            }
        }
    }
}
