package com.nextroom.nextroom.presentation.ui.tutorial.timer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.setFragmentResultListener
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.common.NRTwoButtonDialog
import com.nextroom.nextroom.presentation.extension.assistedViewModel
import com.nextroom.nextroom.presentation.extension.enableFullScreen
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.updateSystemPadding
import com.nextroom.nextroom.presentation.ui.main.ModifyTimeBottomSheet
import com.nextroom.nextroom.presentation.ui.tutorial.TutorialSharedViewModel
import com.nextroom.nextroom.presentation.ui.tutorial.timer.compose.TutorialTimerScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TutorialTimerFragment : ComposeBaseViewModelFragment<TutorialTimerViewModel>() {
    override val screenName: String = "tutorial_timer"

    @Inject
    lateinit var viewModelFactory: TutorialTimerViewModel.Factory

    private val tutorialSharedViewModel: TutorialSharedViewModel by hiltNavGraphViewModels(R.id.tutorial_navigation)

    override val viewModel: TutorialTimerViewModel by assistedViewModel {
        viewModelFactory.create(tutorialSharedViewModel)
    }

    private lateinit var backCallback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Block back press during game, require long-press exit
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.uiState.collectAsState()

                TutorialTimerScreen(
                    state = state,
                    onKeyInput = viewModel::inputHintCode,
                    onBackspace = viewModel::backspaceHintCode,
                    onMemoClick = ::navigateToMemo,
                    onExitLongPress = ::showExitDialog,
                    onTimerLongPress = ::showModifyTimeBottomSheet,
                    onDismissTooltips = viewModel::dismissTooltips
                )
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
                viewModel.uiEvent.collect(::handleEvent)
            }
        }
    }

    private fun handleEvent(event: TutorialTimerEvent) {
        when (event) {
            is TutorialTimerEvent.OpenHint -> {
                tutorialSharedViewModel.setCurrentHint(event.hint)
                findNavController().safeNavigate(
                    TutorialTimerFragmentDirections.moveToTutorialHintFragment()
                )
            }

            is TutorialTimerEvent.TimerFinished -> {
                snackbar(R.string.game_finished)
            }

            is TutorialTimerEvent.ExitTutorial -> {
                tutorialSharedViewModel.finishTutorial()
                findNavController().popBackStack(R.id.login_fragment, false)
            }
        }
    }

    private fun navigateToMemo() {
        findNavController().safeNavigate(
            TutorialTimerFragmentDirections.moveToTutorialMemoFragment()
        )
    }

    private fun showExitDialog() {
        NavGraphDirections.moveToNrTwoButtonDialog(
            NRTwoButtonDialog.NRTwoButtonArgument(
                title = getString(R.string.text_tutorial_exit_dialog_title),
                message = getString(R.string.text_tutorial_exit_dialog_message),
                posBtnText = getString(R.string.dialog_yes),
                negBtnText = getString(R.string.dialog_no),
                dialogKey = REQUEST_KEY_EXIT_TUTORIAL
            )
        ).also { findNavController().safeNavigate(it) }
    }

    private fun showModifyTimeBottomSheet() {
        val currentMinutes = viewModel.uiState.value.totalSeconds / 60
        NavGraphDirections.showModifyTimeBottomSheet(
            requestKey = REQUEST_KEY_MODIFY_TIME,
            timeLimitInMinute = currentMinutes
        ).also { findNavController().safeNavigate(it) }
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(REQUEST_KEY_EXIT_TUTORIAL) { _, _ ->
            viewModel.exitTutorial()
        }
        setFragmentResultListener(REQUEST_KEY_MODIFY_TIME) { _, bundle ->
            val modifiedTime = bundle.getInt(ModifyTimeBottomSheet.BUNDLE_KEY_MODIFIED_TIME)
            tutorialSharedViewModel.modifyTime(modifiedTime)
        }
    }

    override fun onDetach() {
        backCallback.remove()
        super.onDetach()
    }

    companion object {
        const val REQUEST_KEY_EXIT_TUTORIAL = "REQUEST_KEY_EXIT_TUTORIAL"
        const val REQUEST_KEY_MODIFY_TIME = "REQUEST_KEY_MODIFY_TIME"
    }
}
