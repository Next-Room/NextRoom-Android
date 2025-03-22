package com.nextroom.nextroom.presentation.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentEmailLoginBinding
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.setError
import com.nextroom.nextroom.presentation.extension.setStateListener
import com.nextroom.nextroom.presentation.extension.showKeyboard
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.ui.Constants.KAKAO_BUSINESS_CHANNEL_URL
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class EmailLoginFragment : BaseFragment<FragmentEmailLoginBinding>(FragmentEmailLoginBinding::inflate) {

    private val viewModel: EmailLoginViewModel by viewModels()

    private var emailInitialised = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkEmailSaved()
        initViews()
        observe()

        viewModel.observe(viewLifecycleOwner, state = ::render)
    }

    private fun initViews() = with(binding) {
        etEmail.apply {
            setStateListener()
            doAfterTextChanged {
                viewModel.inputCode(it.toString())
            }
            showKeyboard()
        }
        etPassword.apply {
            setStateListener()
            doAfterTextChanged {
                viewModel.inputPassword(it.toString())
            }
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        viewModel.complete()
                        true
                    }

                    else -> false
                }
            }
        }

        tvNoAccountGuide.setOnClickListener { goToOnboardingScreen() }
        btnEmailLogin.setOnClickListener { viewModel.complete() }

        tvPrivacyPolicy.setOnClickListener {
            val action = EmailLoginFragmentDirections.moveToWebViewFragment(getString(R.string.link_privacy_policy))
            findNavController().safeNavigate(action)
        }

        cbIdSave.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onIdSaveChecked(isChecked)
        }

        tvCustomerService.setOnClickListener {
            try {
                viewModel.container.stateFlow.value.kakaoChannelUrl
                    .ifEmpty { KAKAO_BUSINESS_CHANNEL_URL }
                    .let { url ->
                        Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
                    }.also { startActivity(it) }
            } catch (e: Exception) {
                toast(getString(R.string.error_something))
            }
        }
    }

    private fun observe() {
        viewModel.observe(viewLifecycleOwner, sideEffect = ::handleEvent)
        viewLifecycleOwner.repeatOnStarted {
            viewModel.loginState.collect { loggedIn ->
                if (loggedIn) {
                    val action =
                        EmailLoginFragmentDirections.moveToThemeSelectFragment()
                    findNavController().safeNavigate(action)
                    clearInputs()
                }
            }
        }
    }

    private fun render(state: EmailLoginState) = with(binding) {
        pbLoading.isVisible = state.loading
        etEmail.isEnabled = !state.loading
        etPassword.isEnabled = !state.loading
        if (!emailInitialised) {
            emailInitialised = true
            etEmail.setText(state.userEmail)
            cbIdSave.isChecked = state.idSaveChecked
        }
        btnEmailLogin.isEnabled = !state.loading
        tvPrivacyPolicy.isEnabled = !state.loading
        tvNoAccountGuide.isEnabled = !state.loading
    }

    private fun handleEvent(event: EmailLoginEvent) {
        when (event) {
            is EmailLoginEvent.ShowMessage -> snackbar(event.message.toString(requireContext()))
            is EmailLoginEvent.EmailLoginFailed -> {
                binding.etEmail.setError()
                binding.etPassword.setError()
                snackbar(event.message)
            }

            EmailLoginEvent.GoToOnboardingScreen -> {
                goToOnboardingScreen()
            }

            EmailLoginEvent.GoToGameScreen -> Unit
        }
    }

    private fun goToOnboardingScreen() {
        val action = EmailLoginFragmentDirections.moveToOnboardingFragment()
        findNavController().safeNavigate(action)
    }

    private fun clearInputs() {
        binding.etEmail.setText("")
        binding.etPassword.setText("")
        viewModel.initState()
    }
}
