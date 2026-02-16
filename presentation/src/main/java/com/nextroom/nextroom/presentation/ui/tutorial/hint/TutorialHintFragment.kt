package com.nextroom.nextroom.presentation.ui.tutorial.hint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.common.compose.NRColor
import com.nextroom.nextroom.presentation.common.compose.NRToolbar
import com.nextroom.nextroom.presentation.extension.assistedViewModel
import com.nextroom.nextroom.presentation.extension.enableFullScreen
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.updateSystemPadding
import com.nextroom.nextroom.presentation.ui.tutorial.TutorialSharedViewModel
import com.nextroom.nextroom.presentation.ui.tutorial.hint.compose.TutorialHintScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TutorialHintFragment : ComposeBaseViewModelFragment<TutorialHintViewModel>() {
    override val screenName: String = "tutorial_hint"

    @Inject
    lateinit var viewModelFactory: TutorialHintViewModel.Factory

    private val tutorialSharedViewModel: TutorialSharedViewModel by hiltNavGraphViewModels(R.id.tutorial_navigation)

    override val viewModel: TutorialHintViewModel by assistedViewModel {
        viewModelFactory.create(tutorialSharedViewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val state by viewModel.uiState.collectAsState()
                val timerText = remember(state.lastSeconds) {
                    val minutes = state.lastSeconds / 60
                    val seconds = state.lastSeconds % 60
                    "%02d:%02d".format(minutes, seconds)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NRColor.Dark01)
                ) {
                    NRToolbar(
                        title = timerText,
                        onBackClick = ::goBack,
                        rightButtonText = stringResource(R.string.memo_button),
                        onRightButtonClick = ::navigateToMemo
                    )

                    TutorialHintScreen(
                        state = state,
                        onHintOpenClick = { viewModel.openHint() },
                        onAnswerOpenClick = { viewModel.openAnswer() }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enableFullScreen()
        updateSystemPadding(false)
    }

    private fun goBack() {
        findNavController().popBackStack(R.id.tutorial_timer_fragment, false)
    }

    private fun navigateToMemo() {
        findNavController().safeNavigate(
            TutorialHintFragmentDirections.moveToTutorialMemoFragment(fromHint = true)
        )
    }
}
