package com.nextroom.nextroom.presentation.ui.password

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentCheckPasswordBinding
import com.nextroom.nextroom.presentation.extension.BUNDLE_KEY_RESULT_DATA
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckPasswordFragment : BaseFragment<FragmentCheckPasswordBinding>(FragmentCheckPasswordBinding::inflate) {

    private val viewModel: CheckPasswordViewModel by viewModels()
    private val args: CheckPasswordFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
        initSubscribe()
        showBiometricPrompt()
    }

    private fun initView() {
        binding.keyBiometric.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    private fun initSubscribe() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.inputPassword.collect { password ->
                    updateUi(password)
                }
            }
            launch {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        CheckPasswordViewModel.UiEvent.PasswordCorrect -> onPasswordCorrected()
                        CheckPasswordViewModel.UiEvent.PasswordInCorrect -> {
                            binding.customCodeInput.setError()
                            toast(getString(R.string.text_incorrect_password_error_message))
                        }
                    }
                }
            }
        }
    }

    private fun updateUi(password: String) {
        binding.customCodeInput.setCode(password)
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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
    }

    private fun onPasswordCorrected() {
        setFragmentResult(
            requestKey = args.requestKey,
            bundleOf(BUNDLE_KEY_RESULT_DATA to args.resultData)
        )
        findNavController().popBackStack()
    }

    private fun initListener() {
        binding.tvKey1.setOnClickListener { viewModel.onNumberClicked(getString(R.string.text_1)) }
        binding.tvKey2.setOnClickListener { viewModel.onNumberClicked(getString(R.string.text_2)) }
        binding.tvKey3.setOnClickListener { viewModel.onNumberClicked(getString(R.string.text_3)) }
        binding.tvKey4.setOnClickListener { viewModel.onNumberClicked(getString(R.string.text_4)) }
        binding.tvKey5.setOnClickListener { viewModel.onNumberClicked(getString(R.string.text_5)) }
        binding.tvKey6.setOnClickListener { viewModel.onNumberClicked(getString(R.string.text_6)) }
        binding.tvKey7.setOnClickListener { viewModel.onNumberClicked(getString(R.string.text_7)) }
        binding.tvKey8.setOnClickListener { viewModel.onNumberClicked(getString(R.string.text_8)) }
        binding.tvKey9.setOnClickListener { viewModel.onNumberClicked(getString(R.string.text_9)) }
        binding.tvKey0.setOnClickListener { viewModel.onNumberClicked(getString(R.string.text_0)) }
        binding.keyBiometric.setOnClickListener { showBiometricPrompt() }
        binding.keyBackspace.setOnClickListener { viewModel.onBackSpaceClicked() }
        binding.ivBack.setOnClickListener { findNavController().navigateUp() }
    }
}