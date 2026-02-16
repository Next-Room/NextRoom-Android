package com.nextroom.nextroom.presentation.ui.tutorial.memo

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
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.extension.assistedViewModel
import com.nextroom.nextroom.presentation.extension.enableFullScreen
import com.nextroom.nextroom.presentation.extension.updateSystemPadding
import com.nextroom.nextroom.presentation.ui.tutorial.TutorialSharedViewModel
import com.nextroom.nextroom.presentation.ui.tutorial.memo.compose.TutorialMemoScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TutorialMemoFragment : ComposeBaseViewModelFragment<TutorialMemoViewModel>() {
    override val screenName: String = "tutorial_memo"

    @Inject
    lateinit var viewModelFactory: TutorialMemoViewModel.Factory

    private val tutorialSharedViewModel: TutorialSharedViewModel by hiltNavGraphViewModels(R.id.tutorial_navigation)

    override val viewModel: TutorialMemoViewModel by assistedViewModel {
        viewModelFactory.create(tutorialSharedViewModel)
    }

    private val args: TutorialMemoFragmentArgs by navArgs()

    private lateinit var backCallback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {} // Block back press
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

                TutorialMemoScreen(
                    state = state,
                    fromHint = args.fromHint,
                    onBackClick = ::goBack,
                    onHintClick = ::goToHint,
                    onPenClick = viewModel::pickPen,
                    onEraserClick = viewModel::pickEraser,
                    onEraseAllClick = viewModel::eraseAll,
                    onPathsChanged = viewModel::updatePaths
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enableFullScreen()
        updateSystemPadding(false)
    }

    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun goToHint() {
        findNavController().popBackStack(R.id.tutorial_hint_fragment, false)
    }

    override fun onDetach() {
        backCallback.remove()
        super.onDetach()
    }
}
