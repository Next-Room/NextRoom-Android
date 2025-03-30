package com.nextroom.nextroom.presentation.ui.login

import androidx.fragment.app.viewModels
import com.nextroom.nextroom.presentation.base.BaseViewModelFragment
import com.nextroom.nextroom.presentation.databinding.FragmentSignupBinding

class SignupFragment : BaseViewModelFragment<FragmentSignupBinding, SignupViewModel>(FragmentSignupBinding::inflate) {
    override val screenName = "signup"
    override val viewModel: SignupViewModel by viewModels()

    override fun initViews() {
        super.initViews()

        binding.tvSignupComplete.isEnabled = false
    }
}