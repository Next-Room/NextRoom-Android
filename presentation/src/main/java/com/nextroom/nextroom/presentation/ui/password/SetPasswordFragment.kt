package com.nextroom.nextroom.presentation.ui.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.ui.password.compose.SetPasswordScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetPasswordFragment : ComposeBaseViewModelFragment<SetPasswordViewModel>() {

    override val screenName: String = "set_password"
    override val viewModel: SetPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.uiState.collectAsState()
                SetPasswordScreen(
                    state = state,
                    onKeyClick = viewModel::onNumberClicked,
                    onBackspaceClick = viewModel::onBackSpaceClicked,
                    onBackClick = { findNavController().navigateUp() },
                )
            }
        }
    }

    override fun initSubscribe() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        SetPasswordViewModel.UiEvent.SettingPasswordFinished -> {
                            toast(getString(R.string.text_set_password_succeed))
                            findNavController().popBackStack()
                        }

                        SetPasswordViewModel.UiEvent.PasswordNotMatched -> {
                            toast(getString(R.string.text_incorrect_password_error_message))
                        }
                    }
                }
            }
        }
    }
}
