package com.nextroom.nextroom.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
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
        binding.tvApplyFreeTrial.setOnClickListener {
            val action =
                OnboardingFragmentDirections.actionGlobalWebViewFragment("https://nextroom-escape.vercel.app/")
            findNavController().safeNavigate(action)
        }
        binding.tvExistingUserGuide.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
