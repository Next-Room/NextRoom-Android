package com.nextroom.nextroom.presentation.ui.hint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.extension.assistedViewModel
import com.nextroom.nextroom.presentation.extension.enableFullScreen
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.updateSystemPadding
import com.nextroom.nextroom.presentation.ui.hint.compose.HintScreen
import com.nextroom.nextroom.presentation.ui.hint.compose.HintTimerToolbar
import com.nextroom.nextroom.presentation.ui.main.GameSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HintFragment : ComposeBaseViewModelFragment<HintViewModel>() {
    override val screenName: String = "hint"

    @Inject
    lateinit var viewModelFactory: HintViewModel.Factory

    private val gameSharedViewModel: GameSharedViewModel by hiltNavGraphViewModels(R.id.game_navigation)

    override val viewModel: HintViewModel by assistedViewModel {
        viewModelFactory.create(gameSharedViewModel)
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

                Column(modifier = Modifier.fillMaxSize()) {
                    HintTimerToolbar(
                        lastSecondsFlow = viewModel.lastSeconds,
                        onBackClick = ::gotoHome,
                        onMemoClick = ::navigateToMemo
                    )

                    HintScreen(
                        state = state,
                        onHintImageClick = ::navigateToHintImageViewer,
                        onAnswerImageClick = ::navigateToAnswerImageViewer,
                        onHintOpenClick = { viewModel.tryOpenHint(state.hint.id) },
                        onAnswerOpenClick = { gameSharedViewModel.addOpenedAnswerId(state.hint.id) }
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

    override fun initSubscribe() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                gameSharedViewModel.currentHint.collect { hint ->
                    hint?.let { viewModel.setHint(it) }
                }
            }
            launch {
                gameSharedViewModel.subscribeStatus.collect { subscribeStatus ->
                    viewModel.setSubscribeStatus(subscribeStatus)
                }
            }
            launch {
                viewModel.uiEvent.collect(::handleEvent)
            }
        }
    }

    private fun navigateToHintImageViewer(position: Int) {
        val state = viewModel.uiState.value
        if (state.userSubscribeStatus == SubscribeStatus.Subscribed) {
            NavGraphDirections.moveToImageViewerFragment(
                imageUrlList = state.hint.hintImageUrlList.toTypedArray(),
                position = position
            ).also {
                findNavController().safeNavigate(it)
            }
            FirebaseAnalytics.getInstance(requireContext())
                .logEvent("btn_click", bundleOf("btn_name" to "hint_image"))
        }
    }

    private fun navigateToAnswerImageViewer(position: Int) {
        val state = viewModel.uiState.value
        if (state.userSubscribeStatus == SubscribeStatus.Subscribed) {
            NavGraphDirections.moveToImageViewerFragment(
                imageUrlList = state.hint.answerImageUrlList.toTypedArray(),
                position = position
            ).also {
                findNavController().safeNavigate(it)
            }
            FirebaseAnalytics.getInstance(requireContext())
                .logEvent("btn_click", bundleOf("btn_name" to "answer_image"))
        }
    }

    private fun navigateToMemo() {
        val action = HintFragmentDirections.moveToMemoFragment(true)
        findNavController().safeNavigate(action)
    }

    private fun handleEvent(event: HintEvent) {
        when (event) {
            is HintEvent.NetworkError -> snackbar(R.string.error_network)
            is HintEvent.UnknownError -> snackbar(R.string.error_something)
            is HintEvent.ClientError -> snackbar(event.message)
            is HintEvent.HintLimitExceed -> snackbar(R.string.game_hint_limit_exceed)
        }
    }

    private fun gotoHome() {
        findNavController().popBackStack(R.id.timer_fragment, false)
    }
}
