package com.nextroom.nextroom.presentation.ui.login

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observe()
    }

    private fun initViews() = with(binding) {
        etAdminCode.apply {
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
            val action = LoginFragmentDirections.actionGlobalWebViewFragment(
                "https://basalt-cathedral-c81.notion.site/fbb1f04ae70d473380e64d12ed013df8?pvs=4",
            )
            findNavController().safeNavigate(action)
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
                }
            }
        }
    }

    private fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.ShowMessage -> snackbar(event.message.toString(requireContext()))
            is LoginEvent.LoginFailed -> {
                binding.etAdminCode.setError()
                binding.etPassword.setError()
                snackbar(event.message)
            }
            LoginEvent.GoToOnboardingScreen -> {
                goToOnboardingScreen()
            }
        }
    }

    private fun goToOnboardingScreen() {
        val action = LoginFragmentDirections.actionLoginFragmentToOnboardingFragment()
        findNavController().safeNavigate(action)
    }

    override fun onStop() {
        binding.etAdminCode.setText("")
        binding.etPassword.setText("")
        viewModel.initState()
        super.onStop()
    }
}
