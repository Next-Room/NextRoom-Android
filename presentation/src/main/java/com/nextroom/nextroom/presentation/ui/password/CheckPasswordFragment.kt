package com.nextroom.nextroom.presentation.ui.password

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.extension.BUNDLE_KEY_RESULT_DATA
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.ui.password.compose.CheckPasswordScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckPasswordFragment : ComposeBaseViewModelFragment<CheckPasswordViewModel>() {

    override val screenName: String = "check_password"
    override val viewModel: CheckPasswordViewModel by viewModels()
    private val args: CheckPasswordFragmentArgs by navArgs()

    private val showBiometric: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.uiState.collectAsState()
                CheckPasswordScreen(
                    state = state,
                    onKeyClick = viewModel::onNumberClicked,
                    onBackspaceClick = viewModel::onBackSpaceClicked,
                    onBackClick = { findNavController().navigateUp() },
                    onBiometricClick = ::showBiometricPrompt,
                    showBiometric = showBiometric,
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBiometricPrompt()
    }

    override fun initSubscribe() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        CheckPasswordViewModel.UiEvent.PasswordCorrect -> onPasswordCorrected()
                        CheckPasswordViewModel.UiEvent.PasswordInCorrect -> {
                            toast(getString(R.string.text_incorrect_password_error_message))
                        }
                    }
                }
            }
        }
    }

    private fun showBiometricPrompt() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val executor = ContextCompat.getMainExecutor(requireContext())
        BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onPasswordCorrected()
            }
        }).also { biometricPrompt ->
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.text_finger_print_auth))
                .setNegativeButtonText(getString(R.string.text_cancel))
                .build()
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun onPasswordCorrected() {
        setFragmentResult(
            requestKey = args.requestKey,
            bundleOf(BUNDLE_KEY_RESULT_DATA to args.resultData)
        )
        findNavController().popBackStack()
    }
}
