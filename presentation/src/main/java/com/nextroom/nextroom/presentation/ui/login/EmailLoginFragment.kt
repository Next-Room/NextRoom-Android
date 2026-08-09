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
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.nextroom.nextroom.presentation.util.GoogleAuthClient
import com.nextroom.nextroom.presentation.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint
class EmailLoginFragment : ComposeBaseViewModelFragment<EmailLoginViewModel>() {

    override val screenName = "email_login"
    override val viewModel: EmailLoginViewModel by viewModels()

    @Inject
    lateinit var googleAuthClient: GoogleAuthClient

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
                    onGoogleLoginClick = ::requestGoogleAuth,
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

    /**
     * Credential Manager는 계정 선택 UI를 띄우기 위해 Activity를 필요로 한다.
     * Activity 참조가 화면 밖으로 새어나가지 않도록 viewLifecycleOwner 스코프 안에서만 다룬다.
     */
    private fun requestGoogleAuth() {
        viewLifecycleOwner.lifecycleScope.launch {
            // 계정 선택 UI가 뜨기까지 시간이 걸리므로 요청 시작 시점부터 로딩을 노출한다.
            viewModel.setGoogleAuthLoading(true)
            val idToken = try {
                googleAuthClient.requestGoogleIdToken(requireActivity())
            } catch (e: Exception) {
                // 성공하면 loginWithGoogle이 로딩을 이어받으므로, 실패한 경우에만 해제한다.
                viewModel.setGoogleAuthLoading(false)
                when (e) {
                    // 사용자가 계정 선택을 취소한 경우이므로 오류로 처리하지 않는다.
                    is GetCredentialCancellationException -> Unit
                    is CancellationException -> throw e
                    else -> {
                        Logger.e(e)
                        toast(R.string.error_something)
                    }
                }
                return@launch
            }
            viewModel.loginWithGoogle(idToken)
        }
    }

    private fun handleEvent(event: EmailLoginViewModel.UiEvent) {
        when (event) {
            is EmailLoginViewModel.UiEvent.ShowMessage ->
                snackbar(event.message.toString(requireContext()))

            is EmailLoginViewModel.UiEvent.EmailLoginFailed -> snackbar(event.message)

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
