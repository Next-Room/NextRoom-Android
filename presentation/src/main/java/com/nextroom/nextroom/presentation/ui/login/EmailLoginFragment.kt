package com.nextroom.nextroom.presentation.ui.login

import android.content.Intent
import android.net.Uri
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
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.ui.login.compose.EmailLoginScreen
import com.nextroom.nextroom.presentation.ui.onboarding.LoginFragment.Companion.SIGNUP_REQUEST_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailLoginFragment : ComposeBaseViewModelFragment<EmailLoginViewModel>() {

    override val screenName = "email_login"
    override val viewModel: EmailLoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.uiState.collectAsState()
                EmailLoginScreen(
                    state = state,
                    onEmailChange = viewModel::inputEmail,
                    onPasswordChange = viewModel::inputPassword,
                    onEmailSaveCheckedChange = viewModel::onEmailSaveChecked,
                    onLoginClick = viewModel::complete,
                    onGoogleLoginClick = viewModel::requestGoogleAuth,
                    onBackClick = { findNavController().navigateUp() },
                    onCustomerServiceClick = ::openCustomerService,
                    onSignupClick = ::openSignupWebView,
                )
            }
        }
    }

    override fun initSubscribe() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiEvent.collect(::handleEvent)
            }
            launch {
                viewModel.loginState.collect { loggedIn ->
                    if (loggedIn) moveToThemeSelect()
                }
            }
        }
    }

    private fun handleEvent(event: EmailLoginViewModel.UiEvent) {
        when (event) {
            is EmailLoginViewModel.UiEvent.ShowMessage ->
                snackbar(event.message.toString(requireContext()))

            is EmailLoginViewModel.UiEvent.EmailLoginFailed -> snackbar(event.message)

            EmailLoginViewModel.UiEvent.GoogleAuthFailed,
            EmailLoginViewModel.UiEvent.GoogleLoginFailed -> toast(R.string.error_something)

            is EmailLoginViewModel.UiEvent.NeedAdditionalUserInfo -> moveToSignup()
        }
    }

    private fun openCustomerService() {
        try {
            startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(getString(R.string.link_official_instagram))
                }
            )
        } catch (e: Exception) {
            toast(getString(R.string.error_something))
        }
    }

    private fun openSignupWebView() {
        NavGraphDirections.moveToWebViewFragment(
            url = getString(R.string.link_signup),
            showToolbar = true,
        ).also { findNavController().safeNavigate(it) }
    }

    private fun moveToThemeSelect() {
        findNavController().safeNavigate(EmailLoginFragmentDirections.moveToThemeSelectFragment())
    }

    private fun moveToSignup() {
        findNavController().safeNavigate(EmailLoginFragmentDirections.moveToSignup(SIGNUP_REQUEST_KEY))
    }
}
