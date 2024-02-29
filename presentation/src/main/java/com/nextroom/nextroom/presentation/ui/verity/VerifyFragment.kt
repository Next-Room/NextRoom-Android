package com.nextroom.nextroom.presentation.ui.verity

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentAdminCodeBinding
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.setStateListener
import com.nextroom.nextroom.presentation.extension.showKeyboard
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.model.InputState
import com.nextroom.nextroom.presentation.ui.login.LoginEvent
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class VerifyFragment : BaseFragment<FragmentAdminCodeBinding>(FragmentAdminCodeBinding::inflate) {

    private val viewModel: VerifyViewModel by viewModels()
    private val args: VerifyFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observe()
    }

    private fun initViews() = with(binding) {
        etAdminCode.apply {
            setStateListener()
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        viewModel.complete()
                        true
                    }

                    else -> false
                }
            }
            doAfterTextChanged {
                viewModel.inputCode(it.toString())
            }
            showKeyboard()
        }
        btnInput.setOnClickListener { viewModel.complete() }
    }

    private fun observe() {
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    private fun render(state: VerifyState) = with(binding) {
        if (state.inputState is InputState.Ok) {
            val action = VerifyFragmentDirections.actionVerifyFragmentToMainFragment()
            findNavController().safeNavigate(action)
        }
        btnInput.isEnabled = state.currentInput.length == 5
    }

    private fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.LoginFailed -> snackbar(event.message)
            is LoginEvent.ShowMessage -> snackbar(event.message.toString(requireContext()))
            LoginEvent.GoToOnboardingScreen -> Unit
            LoginEvent.GoToMainScreen -> {
                val action = VerifyFragmentDirections.actionVerifyFragmentToMainFragment()
                findNavController().safeNavigate(action)
            }
        }
    }
}
