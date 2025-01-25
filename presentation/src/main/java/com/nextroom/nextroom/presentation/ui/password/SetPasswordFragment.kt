package com.nextroom.nextroom.presentation.ui.password

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentSetPasswordBinding
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetPasswordFragment : BaseFragment<FragmentSetPasswordBinding>(FragmentSetPasswordBinding::inflate) {
    private val viewModel: SetPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initSubscribe()
    }

    private fun initSubscribe() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiState.collect { state ->
                    updateUi(state)
                }
            }
            launch {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        SetPasswordViewModel.UiEvent.SettingPasswordFinished -> {
                            toast(getString(R.string.text_set_password_succeed))
                            findNavController().popBackStack()
                        }

                        SetPasswordViewModel.UiEvent.PasswordNotMatched -> {
                            binding.customCodeInput.setError()
                            toast(getString(R.string.text_incorrect_password_error_message))
                        }
                    }
                }
            }
        }
    }

    private fun updateUi(state: SetPasswordViewModel.UiState) {
        binding.customCodeInput.setCode(state.displayPassword)
        when (state.step) {
            SetPasswordViewModel.UiState.Step.PasswordSetting -> {
                binding.tvHeader.text = getString(R.string.text_set_password)
                binding.tvDescription.text = getString(R.string.text_set_password_description)
            }

            SetPasswordViewModel.UiState.Step.PasswordConfirm -> {
                binding.tvHeader.text = getString(R.string.text_confirm_password)
                binding.tvDescription.text = getString(R.string.text_set_password_description_for_confirm)
            }
        }
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
        binding.keyBackspace.setOnClickListener { viewModel.onBackSpaceClicked() }
    }
}