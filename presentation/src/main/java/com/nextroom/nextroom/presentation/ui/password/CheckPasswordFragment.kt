package com.nextroom.nextroom.presentation.ui.password

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentCheckPasswordBinding
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

        initListener()
        initSubscribe()
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
                        CheckPasswordViewModel.UiEvent.PasswordCorrect -> {
                            setFragmentResult(requestKey = args.requestKey, bundleOf())
                            findNavController().popBackStack()
                        }

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