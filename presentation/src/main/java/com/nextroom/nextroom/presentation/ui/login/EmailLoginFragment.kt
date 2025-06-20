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
import com.nextroom.nextroom.presentation.NavGraphDirections
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
import com.nextroom.nextroom.presentation.ui.onboarding.LoginFragment.Companion.SIGNUP_REQUEST_KEY
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

        tvEmailLogin.setOnClickListener { viewModel.complete() }
        llGoogleLogin.setOnClickListener { viewModel.requestGoogleAuth() }

        cbIdSave.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onIdSaveChecked(isChecked)
        }

        tvCustomerService.setOnClickListener {
            try {
                getString(R.string.link_official_instagram).let { url ->
                    Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
                }.also { startActivity(it) }
            } catch (e: Exception) {
                toast(getString(R.string.error_something))
            }
        }

        ivBack.setOnClickListener { findNavController().navigateUp() }
        tvCustomerService.setOnClickListener {
            try {
                Intent(Intent.ACTION_VIEW)
                    .apply { data = Uri.parse(getString(R.string.link_official_instagram)) }
                    .also { startActivity(it) }
            } catch (e: Exception) {
                toast(getString(R.string.error_something))
            }
        }
        tvSignup.setOnClickListener {
            NavGraphDirections.moveToWebViewFragment(
                url = getString(R.string.link_signup),
                showToolbar = true,
            ).also { findNavController().safeNavigate(it) }
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
        tvEmailLogin.isEnabled = !state.loading
    }

    private fun handleEvent(event: EmailLoginEvent) {
        when (event) {
            is EmailLoginEvent.ShowMessage -> snackbar(event.message.toString(requireContext()))
            is EmailLoginEvent.EmailLoginFailed -> {
                binding.etEmail.setError()
                binding.etPassword.setError()
                snackbar(event.message)
            }

            EmailLoginEvent.GoToGameScreen -> Unit
            EmailLoginEvent.GoogleAuthFailed,
            EmailLoginEvent.GoogleLoginFailed -> toast(R.string.error_something)

            is EmailLoginEvent.NeedAdditionalUserInfo -> moveToSignup()
        }
    }

    private fun clearInputs() {
        binding.etEmail.setText("")
        binding.etPassword.setText("")
        viewModel.initState()
    }

    private fun moveToSignup() {
        findNavController().safeNavigate(EmailLoginFragmentDirections.moveToSignup(SIGNUP_REQUEST_KEY))
    }
}
