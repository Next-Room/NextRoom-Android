package com.nextroom.nextroom.presentation.ui.login

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentLoginBinding
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.setError
import com.nextroom.nextroom.presentation.extension.setStateListener
import com.nextroom.nextroom.presentation.extension.showKeyboard
import com.nextroom.nextroom.presentation.extension.snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()

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
        btnLogin.setOnClickListener { viewModel.complete() }

        tvPrivacyPolicy.setOnClickListener {
            val action = LoginFragmentDirections.actionGlobalWebViewFragment(getString(R.string.link_privacy_policy))
            findNavController().safeNavigate(action)
        }

        cbIdSave.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onIdSaveChecked(isChecked)
        }
    }

    private fun observe() {
        viewModel.observe(viewLifecycleOwner, sideEffect = ::handleEvent)
        viewLifecycleOwner.repeatOnStarted {
            viewModel.loginState.collect { loggedIn ->
                if (loggedIn) {
                    val action =
                        LoginFragmentDirections.actionLoginFragmentToAdminMainFragment()
                    findNavController().safeNavigate(action)
                    clearInputs()
                }
            }
        }
    }

    private fun render(state: LoginState) = with(binding) {
        pbLoading.isVisible = state.loading
        etEmail.isEnabled = !state.loading
        etPassword.isEnabled = !state.loading
        if (!emailInitialised) {
            emailInitialised = true
            etEmail.setText(state.userEmail)
            cbIdSave.isChecked = state.idSaveChecked
        }
        btnLogin.isEnabled = !state.loading
        tvPrivacyPolicy.isEnabled = !state.loading
        tvNoAccountGuide.isEnabled = !state.loading
    }

    private fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.ShowMessage -> snackbar(event.message.toString(requireContext()))
            is LoginEvent.LoginFailed -> {
                binding.etEmail.setError()
                binding.etPassword.setError()
                snackbar(event.message)
            }

            LoginEvent.GoToOnboardingScreen -> {
                goToOnboardingScreen()
            }

            LoginEvent.GoToGameScreen -> Unit
        }
    }

    private fun goToOnboardingScreen() {
        val action = LoginFragmentDirections.actionLoginFragmentToOnboardingFragment()
        findNavController().safeNavigate(action)
    }

    private fun clearInputs() {
        binding.etEmail.setText("")
        binding.etPassword.setText("")
        viewModel.initState()
    }
}
