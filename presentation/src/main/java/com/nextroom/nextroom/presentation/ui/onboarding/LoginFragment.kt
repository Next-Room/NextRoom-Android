package com.nextroom.nextroom.presentation.ui.onboarding

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.base.BaseViewModelFragment
import com.nextroom.nextroom.presentation.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseViewModelFragment<FragmentLoginBinding, LoginViewModel>(FragmentLoginBinding::inflate),
    View.OnClickListener {
    override val screenName = "login"
    override val viewModel: LoginViewModel by viewModels()

    override fun initListeners() {
        super.initListeners()
        binding.tvStartWithEmail.setOnClickListener(this)
    }

    private fun moveToEmailLogin() {
        LoginFragmentDirections.moveToEmailLogin().also { findNavController().navigate(it) }
    }

    override fun onClick(v: View) {
        when (v) {
            binding.tvStartWithEmail -> moveToEmailLogin()
        }
    }
}