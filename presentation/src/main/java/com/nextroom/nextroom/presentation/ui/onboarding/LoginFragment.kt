package com.nextroom.nextroom.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.ui.onboarding.compose.LoginScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : ComposeBaseViewModelFragment<LoginViewModel>() {
    override val screenName = "login"
    override val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val isLoading by viewModel.apiLoading.collectAsState()
                LoginScreen(
                    isLoading = isLoading,
                    onGoogleLoginClick = viewModel::requestGoogleAuth,
                    onEmailLoginClick = ::moveToEmailLogin,
                    onTryWithoutLoginClick = ::moveToTutorial,
                )
            }
        }
    }

    override fun setFragmentResultListeners() {
        super.setFragmentResultListeners()

        setFragmentResultListener(SIGNUP_REQUEST_KEY) { _, _ ->
            moveToThemeSelect()
        }
    }

    override fun initSubscribe() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is LoginViewModel.UIEvent.NeedAdditionalUserInfo -> moveToSignup()
                    }
                }
            }
            launch {
                viewModel.loginState.collect {
                    if (it) moveToThemeSelect()
                }
            }
        }
    }

    private fun moveToThemeSelect() {
        LoginFragmentDirections.moveToThemeSelect().also { findNavController().safeNavigate(it) }
    }

    private fun moveToEmailLogin() {
        LoginFragmentDirections.moveToEmailLogin().also { findNavController().safeNavigate(it) }
    }

    private fun moveToSignup() {
        LoginFragmentDirections.moveToSignup(SIGNUP_REQUEST_KEY).also { findNavController().safeNavigate(it) }
    }

    private fun moveToTutorial() {
        LoginFragmentDirections.moveToTutorial().also { findNavController().safeNavigate(it) }
    }

    companion object {
        const val SIGNUP_REQUEST_KEY = "SIGNUP_REQUEST_KEY"
    }
}
