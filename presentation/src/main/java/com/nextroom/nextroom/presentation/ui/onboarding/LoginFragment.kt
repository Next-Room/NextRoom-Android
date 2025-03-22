package com.nextroom.nextroom.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentLoginBinding
import com.nextroom.nextroom.presentation.extension.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        binding.btnSignUp.setOnClickListener {
            val action = LoginFragmentDirections.moveToWebViewFragment(
                url = getString(R.string.link_signup),
                showToolbar = true,
            )
            findNavController().safeNavigate(action)
        }
        binding.tvExistingUserGuide.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
