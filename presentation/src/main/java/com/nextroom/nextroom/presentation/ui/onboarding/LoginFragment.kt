package com.nextroom.nextroom.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.ui.onboarding.compose.LoginScreen
import com.nextroom.nextroom.presentation.util.GoogleAuthClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint
class LoginFragment : ComposeBaseViewModelFragment<LoginViewModel>() {
    override val screenName = "login"
    override val viewModel: LoginViewModel by viewModels()

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
                val isLoading by viewModel.apiLoading.collectAsState()
                LoginScreen(
                    isLoading = isLoading,
                    onGoogleLoginClick = ::requestGoogleAuth,
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
                    else -> viewModel.handleError(e)
                }
                return@launch
            }
            viewModel.loginWithGoogle(idToken)
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
