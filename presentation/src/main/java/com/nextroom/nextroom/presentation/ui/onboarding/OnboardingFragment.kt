package com.nextroom.nextroom.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.BuildConfig
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentOnboardingBinding
import com.nextroom.nextroom.presentation.extension.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>(FragmentOnboardingBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        binding.btnSignUp.setOnClickListener {
            val url = if (BuildConfig.DEBUG) "https://dev.nextroom.co.kr/signup" else "https://nextroom.co.kr/signup"
            val action =
                OnboardingFragmentDirections.actionGlobalWebViewFragment(url)
            findNavController().safeNavigate(action)
        }
        binding.tvExistingUserGuide.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
