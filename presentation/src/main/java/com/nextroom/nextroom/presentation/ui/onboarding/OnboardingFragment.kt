package com.nextroom.nextroom.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nextroom.nextroom.presentation.databinding.FragmentOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding
        get() = _binding ?: error("binding is null")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        binding.tvApplyFreeTrial.setOnClickListener {
            // TODO JH: 웹뷰로 홈페이지 이동
        }
        binding.tvExistingUserGuide.setOnClickListener {
            // TODO JH: 로그인 페이지로 이동
        }
    }
}