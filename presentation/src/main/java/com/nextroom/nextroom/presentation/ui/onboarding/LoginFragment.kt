package com.nextroom.nextroom.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseViewModelFragment
import com.nextroom.nextroom.presentation.databinding.FragmentLoginBinding
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseViewModelFragment<FragmentLoginBinding, LoginViewModel>(FragmentLoginBinding::inflate),
    View.OnClickListener {
    override val screenName = "login"
    override val viewModel: LoginViewModel by viewModels()

    override fun initListeners() {
        super.initListeners()
        binding.tvStartWithEmail.setOnClickListener(this)
        binding.llStartWithGoogle.setOnClickListener(this)
    }

    override fun setFragmentResultListeners() {
        super.setFragmentResultListeners()

        setFragmentResultListener(SIGNUP_REQUEST_KEY, ::handleFragmentResults)
    }

    private fun handleFragmentResults(requestKey: String, bundle: Bundle) {
        when (requestKey) {
            SIGNUP_REQUEST_KEY -> moveToThemeSelect()
        }
    }

    override fun initObserve() {
        super.initObserve()

        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.apiLoading.collect {
                    binding.pbLoading.isVisible = it
                }
            }
            launch {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        LoginViewModel.UIEvent.GoogleAuthFailed,
                        LoginViewModel.UIEvent.GoogleLoginFailed -> toast(R.string.error_something)
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

    override fun onClick(v: View) {
        when (v) {
            binding.tvStartWithEmail -> moveToEmailLogin()
            binding.llStartWithGoogle -> viewModel.requestGoogleAuth()
        }
    }

    companion object {
        const val SIGNUP_REQUEST_KEY = "SIGNUP_REQUEST_KEY"
    }
}