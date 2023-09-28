package com.nexters.nextroom.presentation.ui.hint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nexters.nextroom.presentation.R
import com.nexters.nextroom.presentation.extension.safeNavigate
import com.nexters.nextroom.presentation.extension.toTimerFormat
import com.nexters.nextroom.presentation.ui.component.NRToolbar
import com.nexters.nextroom.presentation.ui.component.ToolbarAction
import com.nexters.nextroom.presentation.ui.component.button.MainButton
import com.nexters.nextroom.presentation.ui.main.GameScreenState
import com.nexters.nextroom.presentation.ui.main.GameViewModel
import com.nexters.nextroom.presentation.ui.main.HintScreenComposable
import com.nexters.nextroom.presentation.ui.theme.Gray01
import com.nexters.nextroom.presentation.ui.theme.Gray02
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import timber.log.Timber

@AndroidEntryPoint
class HintFragment : Fragment() {

    private val gameViewModel: GameViewModel by activityViewModels()
    private var scrolled: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                NextRoomTheme {
                    val state by gameViewModel.collectAsState()
                    Surface {
                        Scaffold(
                            topBar = {
                                NRToolbar(
                                    title = state.lastSeconds.toTimerFormat(),
                                    showBackButton = true,
                                    action = object : ToolbarAction {
                                        override val label = "MEMO"
                                        override fun onAction() {
                                            val action = HintFragmentDirections.actionGlobalMemoFragment(true)
                                            findNavController().safeNavigate(action)
                                        }
                                    },
                                    onBackClicked = { gotoHome() },
                                )
                            },
                        ) {
                            HintScreen(
                                Modifier
                                    .fillMaxSize()
                                    .padding(it),
                                state,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HintScreen(modifier: Modifier = Modifier, state: GameScreenState) {
        state.currentHint ?: return
        Box(modifier) {
            HintContent(modifier = Modifier.fillMaxWidth(), state)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .align(Alignment.BottomCenter),
            ) {
                MainButton(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = stringResource(
                        id = if (state.answerOpened) R.string.game_hint_button_goto_home else R.string.game_hint_button_show_answer,
                    ),
                ) {
                    if (state.answerOpened) {
                        gotoHome()
                    } else {
                        // 정답 보기
                        gameViewModel.openAnswer(state.currentHint.hintId)
                    }
                }
            }
        }
    }

    @Composable
    fun HintContent(modifier: Modifier, state: GameScreenState) {
        val lazyListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 20.dp),
            state = lazyListState,
        ) {
            items(state.composables) {
                when (it) {
                    is HintScreenComposable.Content -> Text(
                        modifier = Modifier.padding(top = it.top, bottom = it.bottom),
                        text = it.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray01,
                    )

                    is HintScreenComposable.Divider -> Divider(
                        modifier = Modifier.padding(top = it.top, bottom = it.bottom),
                        color = Gray02,
                    )

                    is HintScreenComposable.Image -> Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            modifier = Modifier.padding(top = it.top, bottom = it.bottom).align(Alignment.Center),
                            painter = painterResource(id = it.drawableRes),
                            contentDescription = "힌트 아이콘",
                        )
                    }

                    is HintScreenComposable.Progress -> Text(
                        modifier = Modifier.padding(top = it.top, bottom = it.bottom),
                        text = String.format("%d%%", it.progress),
                        style = MaterialTheme.typography.titleLarge,
                        color = Gray01,
                    )

                    is HintScreenComposable.Title -> Text(
                        modifier = Modifier.padding(top = it.top, bottom = it.bottom),
                        text = stringResource(it.stringRes),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.size(height = 400.dp, width = 0.dp))
            }
            coroutineScope.launch {
                if (state.answerOpened && !scrolled) {
                    scrolled = true
                    delay(300)
                    lazyListState.animateScrollToItem(
                        index = state.composables.indexOfFirst { it is HintScreenComposable.Title && it.stringRes == R.string.game_answer },
                    )
                }
            }
        }
    }

    private fun gotoHome() {
        Timber.d("gotoHome")
        val action = HintFragmentDirections.actionHintFragmentToMainFragment()
        findNavController().safeNavigate(action)
    }
}
